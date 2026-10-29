package com.example.data.prayer

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

data class CityLocation(
    val name: String,
    val province: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

data class PrayerSchedule(
    val cityName: String,
    val dateFormatted: String,
    val imsak: String,
    val subuh: String,
    val terbit: String,
    val dhuha: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val countdownMinutes: Long
)

data class QiblaInfo(
    val qiblaBearing: Float,      // Angle from True North (degrees)
    val distanceKm: Double,       // Distance to Kaaba in kilometers
    val cityName: String
)

object PrayerCalculator {

    // Kaaba Coordinates
    const val KAABA_LAT = 21.422487
    const val KAABA_LNG = 39.826206

    val MAJOR_CITIES: List<CityLocation> = listOf(
        CityLocation("DKI Jakarta", "DKI Jakarta", -6.2088, 106.8456, 7.0),
        CityLocation("Surabaya", "Jawa Timur", -7.2575, 112.7521, 7.0),
        CityLocation("Bandung", "Jawa Barat", -6.9175, 107.6191, 7.0),
        CityLocation("Medan", "Sumatera Utara", 3.5952, 98.6722, 7.0),
        CityLocation("Semarang", "Jawa Tengah", -6.9667, 110.4167, 7.0),
        CityLocation("Yogyakarta", "D.I. Yogyakarta", -7.7956, 110.3695, 7.0),
        CityLocation("Makassar", "Sulawesi Selatan", -5.1477, 119.4327, 8.0),
        CityLocation("Palembang", "Sumatera Selatan", -2.9761, 104.7754, 7.0),
        CityLocation("Banda Aceh", "Aceh", 5.5483, 95.3238, 7.0),
        CityLocation("Padang", "Sumatera Barat", -0.9471, 100.4172, 7.0),
        CityLocation("Denpasar", "Bali", -8.6705, 115.2126, 8.0),
        CityLocation("Banjarmasin", "Kalimantan Selatan", -3.3167, 114.5833, 8.0),
        CityLocation("Samarinda", "Kalimantan Timur", -0.5022, 117.1536, 8.0),
        CityLocation("Pontianak", "Kalimantan Barat", -0.0263, 109.3425, 7.0),
        CityLocation("Mataram", "Nusa Tenggara Barat", -8.5768, 116.1011, 8.0),
        CityLocation("Jayapura", "Papua", -2.5337, 140.7181, 9.0)
    )

    fun calculateQibla(latitude: Double, longitude: Double, cityName: String = "Lokasi Anda"): QiblaInfo {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val deltaLambda = Math.toRadians(KAABA_LNG - longitude)

        val y = sin(deltaLambda)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(deltaLambda)
        var qiblaRad = atan2(y, x)
        var qiblaDeg = Math.toDegrees(qiblaRad)
        if (qiblaDeg < 0) qiblaDeg += 360.0

        // Distance using Haversine
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(KAABA_LAT - latitude)
        val dLon = Math.toRadians(KAABA_LNG - longitude)
        val a = sin(dLat / 2).pow(2.0) + cos(phi1) * cos(phi2) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceKm = earthRadius * c

        return QiblaInfo(
            qiblaBearing = qiblaDeg.toFloat(),
            distanceKm = distanceKm,
            cityName = cityName
        )
    }

    fun calculatePrayerSchedule(
        city: CityLocation,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerSchedule {
        val lat = city.latitude
        val lng = city.longitude
        val tz = city.timezoneOffsetHours

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day
        val jd = julianDay(year, month, day) - (lng / (360.0 * 24.0))

        // Sun declination & equation of time
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqt = q / 15.0 - fixHour(ra)

        // Midday (Dzuhur) in hours
        val noon = fixHour(12.0 + tz - (lng / 15.0) - eqt)

        // Sun angles (Kemenag standards)
        // Subuh: -20°, Isya: -18°, Sunrise/Sunset: -0.833°, Dhuha: 4.5°
        val subuhHour = noon - sunAngleTime(-20.0, lat, decl)
        val sunriseHour = noon - sunAngleTime(-0.833, lat, decl)
        val dhuhaHour = noon - sunAngleTime(4.5, lat, decl)
        val asrHour = noon + asrTime(1, lat, decl)
        val sunsetHour = noon + sunAngleTime(-0.833, lat, decl)
        val isyaHour = noon + sunAngleTime(-18.0, lat, decl)

        // Add standard 2-minute ihtiyat (safety buffer)
        val ihtiyat = 2.0 / 60.0
        val subuhFormatted = formatTime(subuhHour + ihtiyat)
        val imsakFormatted = formatTime(subuhHour + ihtiyat - (10.0 / 60.0))
        val sunriseFormatted = formatTime(sunriseHour)
        val dhuhaFormatted = formatTime(dhuhaHour + ihtiyat)
        val dzuhurFormatted = formatTime(noon + ihtiyat)
        val asrFormatted = formatTime(asrHour + ihtiyat)
        val maghribFormatted = formatTime(sunsetHour + ihtiyat)
        val isyaFormatted = formatTime(isyaHour + ihtiyat)

        // Calculate countdown to next prayer
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        val prayerTimesMinutes = listOf(
            "Subuh" to timeToMinutes(subuhFormatted),
            "Dzuhur" to timeToMinutes(dzuhurFormatted),
            "Ashar" to timeToMinutes(asrFormatted),
            "Maghrib" to timeToMinutes(maghribFormatted),
            "Isya" to timeToMinutes(isyaFormatted)
        )

        var nextName = "Subuh"
        var nextTime = subuhFormatted
        var diffMinutes = 0L

        val upcoming = prayerTimesMinutes.firstOrNull { it.second > currentTotalMinutes }
        if (upcoming != null) {
            nextName = upcoming.first
            nextTime = when (upcoming.first) {
                "Subuh" -> subuhFormatted
                "Dzuhur" -> dzuhurFormatted
                "Ashar" -> asrFormatted
                "Maghrib" -> maghribFormatted
                "Isya" -> isyaFormatted
                else -> subuhFormatted
            }
            diffMinutes = (upcoming.second - currentTotalMinutes).toLong()
        } else {
            // Next is tomorrow's Subuh
            nextName = "Subuh (Besok)"
            nextTime = subuhFormatted
            val tomorrowSubuh = 24 * 60 + timeToMinutes(subuhFormatted)
            diffMinutes = (tomorrowSubuh - currentTotalMinutes).toLong()
        }

        val months = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val dateFormatted = "$day ${months[calendar.get(Calendar.MONTH)]} $year"

        return PrayerSchedule(
            cityName = "${city.name}, ${city.province}",
            dateFormatted = dateFormatted,
            imsak = imsakFormatted,
            subuh = subuhFormatted,
            terbit = sunriseFormatted,
            dhuha = dhuhaFormatted,
            dzuhur = dzuhurFormatted,
            ashar = asrFormatted,
            maghrib = maghribFormatted,
            isya = isyaFormatted,
            nextPrayerName = nextName,
            nextPrayerTime = nextTime,
            countdownMinutes = diffMinutes
        )
    }

    private fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size >= 2) {
            val h = parts[0].trim().toIntOrNull() ?: 0
            val m = parts[1].trim().toIntOrNull() ?: 0
            return h * 60 + m
        }
        return 0
    }

    private fun sunAngleTime(angle: Double, lat: Double, decl: Double): Double {
        val latR = Math.toRadians(lat)
        val declR = Math.toRadians(decl)
        val angleR = Math.toRadians(angle)
        val cosHourAngle = (sin(angleR) - sin(latR) * sin(declR)) / (cos(latR) * cos(declR))
        if (cosHourAngle > 1.0) return 0.0
        if (cosHourAngle < -1.0) return 12.0
        return Math.toDegrees(acos(cosHourAngle)) / 15.0
    }

    private fun asrTime(factor: Int, lat: Double, decl: Double): Double {
        val latR = Math.toRadians(lat)
        val declR = Math.toRadians(decl)
        val d = -Math.toDegrees(atan(1.0 / (factor + tan(abs(latR - declR)))))
        return sunAngleTime(d, lat, decl)
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    private fun fixHour(a: Double): Double {
        var res = a - 24.0 * floor(a / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun formatTime(hours: Double): String {
        var h = fixHour(hours)
        val totalMinutes = round(h * 60.0).toInt()
        val hour = (totalMinutes / 60) % 24
        val minute = totalMinutes % 60
        return String.format(Locale.US, "%02d:%02d", hour, minute)
    }
}
