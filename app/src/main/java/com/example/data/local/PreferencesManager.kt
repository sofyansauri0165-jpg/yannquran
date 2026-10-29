package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Bookmark
import com.example.data.model.LastRead
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("yann_quran_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_READ_SURAH = "last_read_surah"
        private const val KEY_LAST_READ_SURAH_NAME = "last_read_surah_name"
        private const val KEY_LAST_READ_AYAH = "last_read_ayah"
        private const val KEY_LAST_READ_TOTAL = "last_read_total"
        private const val KEY_LAST_READ_TIME = "last_read_time"

        private const val KEY_BOOKMARKS = "user_bookmarks"
        private const val KEY_FONT_SIZE = "arabic_font_size"
        private const val KEY_SHOW_LATIN = "show_latin"
        private const val KEY_SHOW_TRANSLATION = "show_translation"
        private const val KEY_SELECTED_RECITER = "selected_reciter"
        private const val KEY_SELECTED_CITY_INDEX = "selected_city_index"
        private const val KEY_TASBIH_COUNT = "tasbih_count"
        private const val KEY_TASBIH_TARGET = "tasbih_target"

        // Prayer audio & GPS settings
        private const val KEY_PRAYER_AUDIO_ENABLED = "prayer_audio_enabled"
        private const val KEY_PRAYER_AUDIO_VOLUME = "prayer_audio_volume"
        private const val KEY_REMINDER_TYPE = "reminder_type"
        private const val KEY_USE_GPS = "use_gps_location"
        private const val KEY_GPS_LAT = "gps_latitude"
        private const val KEY_GPS_LNG = "gps_longitude"
        private const val KEY_GPS_NAME = "gps_location_name"
        private const val KEY_TRIGGERED_PREFIX = "prayer_triggered_"

        // Per-prayer audio toggles
        private const val KEY_AUDIO_IMSAK = "audio_imsak"
        private const val KEY_AUDIO_SUBUH = "audio_subuh"
        private const val KEY_AUDIO_TERBIT = "audio_terbit"
        private const val KEY_AUDIO_DZUHUR = "audio_dzuhur"
        private const val KEY_AUDIO_ASHAR = "audio_ashar"
        private const val KEY_AUDIO_MAGHRIB = "audio_maghrib"
        private const val KEY_AUDIO_ISYA = "audio_isya"
    }

    fun saveLastRead(surahNumber: Int, surahName: String, ayahNumber: Int, totalAyat: Int = 0) {
        prefs.edit()
            .putInt(KEY_LAST_READ_SURAH, surahNumber)
            .putString(KEY_LAST_READ_SURAH_NAME, surahName)
            .putInt(KEY_LAST_READ_AYAH, ayahNumber)
            .putInt(KEY_LAST_READ_TOTAL, totalAyat)
            .putLong(KEY_LAST_READ_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getLastRead(): LastRead? {
        val surah = prefs.getInt(KEY_LAST_READ_SURAH, -1)
        if (surah == -1) return null
        val name = prefs.getString(KEY_LAST_READ_SURAH_NAME, "") ?: ""
        val ayah = prefs.getInt(KEY_LAST_READ_AYAH, 1)
        val total = prefs.getInt(KEY_LAST_READ_TOTAL, 0)
        val time = prefs.getLong(KEY_LAST_READ_TIME, System.currentTimeMillis())
        return LastRead(surah, name, ayah, total, time)
    }

    fun getBookmarks(): List<Bookmark> {
        val raw = prefs.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        val list = mutableListOf<Bookmark>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Bookmark(
                        id = obj.optString("id", "${obj.getInt("surah")}_${obj.getInt("ayah")}"),
                        surahNumber = obj.getInt("surah"),
                        surahName = obj.optString("name", "Surah"),
                        ayahNumber = obj.getInt("ayah"),
                        textArab = obj.optString("arab", ""),
                        textIndo = obj.optString("indo", ""),
                        timestamp = obj.optLong("time", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addBookmark(bookmark: Bookmark) {
        val current = getBookmarks().toMutableList()
        current.removeAll { it.surahNumber == bookmark.surahNumber && it.ayahNumber == bookmark.ayahNumber }
        current.add(0, bookmark)
        saveBookmarks(current)
    }

    fun removeBookmark(surahNumber: Int, ayahNumber: Int) {
        val current = getBookmarks().toMutableList()
        current.removeAll { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
        saveBookmarks(current)
    }

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return getBookmarks().any { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
    }

    private fun saveBookmarks(list: List<Bookmark>) {
        val array = JSONArray()
        list.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("surah", b.surahNumber)
                put("name", b.surahName)
                put("ayah", b.ayahNumber)
                put("arab", b.textArab)
                put("indo", b.textIndo)
                put("time", b.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_BOOKMARKS, array.toString()).apply()
    }

    var arabicFontSize: Float
        get() = prefs.getFloat(KEY_FONT_SIZE, 26f)
        set(value) = prefs.edit().putFloat(KEY_FONT_SIZE, value).apply()

    var showLatin: Boolean
        get() = prefs.getBoolean(KEY_SHOW_LATIN, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_LATIN, value).apply()

    var showTranslation: Boolean
        get() = prefs.getBoolean(KEY_SHOW_TRANSLATION, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_TRANSLATION, value).apply()

    var selectedReciter: String
        get() = prefs.getString(KEY_SELECTED_RECITER, "05") ?: "05"
        set(value) = prefs.edit().putString(KEY_SELECTED_RECITER, value).apply()

    var selectedCityIndex: Int
        get() = prefs.getInt(KEY_SELECTED_CITY_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_SELECTED_CITY_INDEX, value).apply()

    var tasbihCount: Int
        get() = prefs.getInt(KEY_TASBIH_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_TASBIH_COUNT, value).apply()

    var tasbihTarget: Int
        get() = prefs.getInt(KEY_TASBIH_TARGET, 33)
        set(value) = prefs.edit().putInt(KEY_TASBIH_TARGET, value).apply()

    // Prayer Audio Preferences
    var isPrayerAudioEnabled: Boolean
        get() = prefs.getBoolean(KEY_PRAYER_AUDIO_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_PRAYER_AUDIO_ENABLED, value).apply()

    var prayerAudioVolume: Float
        get() = prefs.getFloat(KEY_PRAYER_AUDIO_VOLUME, 0.85f)
        set(value) = prefs.edit().putFloat(KEY_PRAYER_AUDIO_VOLUME, value).apply()

    var reminderType: String
        get() = prefs.getString(KEY_REMINDER_TYPE, "AUDIO_FULL") ?: "AUDIO_FULL"
        set(value) = prefs.edit().putString(KEY_REMINDER_TYPE, value).apply()

    // Per-prayer toggles
    fun isPrayerAudioActive(prayerName: String): Boolean {
        if (!isPrayerAudioEnabled) return false
        val key = when (prayerName.trim().lowercase()) {
            "imsak" -> KEY_AUDIO_IMSAK
            "subuh" -> KEY_AUDIO_SUBUH
            "terbit" -> KEY_AUDIO_TERBIT
            "dzuhur" -> KEY_AUDIO_DZUHUR
            "ashar" -> KEY_AUDIO_ASHAR
            "maghrib" -> KEY_AUDIO_MAGHRIB
            "isya" -> KEY_AUDIO_ISYA
            else -> return false
        }
        return prefs.getBoolean(key, true)
    }

    fun setPrayerAudioActive(prayerName: String, active: Boolean) {
        val key = when (prayerName.trim().lowercase()) {
            "imsak" -> KEY_AUDIO_IMSAK
            "subuh" -> KEY_AUDIO_SUBUH
            "terbit" -> KEY_AUDIO_TERBIT
            "dzuhur" -> KEY_AUDIO_DZUHUR
            "ashar" -> KEY_AUDIO_ASHAR
            "maghrib" -> KEY_AUDIO_MAGHRIB
            "isya" -> KEY_AUDIO_ISYA
            else -> return
        }
        prefs.edit().putBoolean(key, active).apply()
    }

    // Deduplication check: Ensure each scheduled prayer audio triggers at most once per calendar date
    fun isPrayerTriggeredToday(dateKey: String, prayerName: String): Boolean {
        return prefs.getBoolean("${KEY_TRIGGERED_PREFIX}${dateKey}_${prayerName.lowercase()}", false)
    }

    fun markPrayerTriggeredToday(dateKey: String, prayerName: String) {
        prefs.edit().putBoolean("${KEY_TRIGGERED_PREFIX}${dateKey}_${prayerName.lowercase()}", true).apply()
    }

    // GPS Location State
    var useGpsLocation: Boolean
        get() = prefs.getBoolean(KEY_USE_GPS, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_GPS, value).apply()

    var gpsLatitude: Double
        get() = java.lang.Double.longBitsToDouble(prefs.getLong(KEY_GPS_LAT, java.lang.Double.doubleToLongBits(-6.2088)))
        set(value) = prefs.edit().putLong(KEY_GPS_LAT, java.lang.Double.doubleToLongBits(value)).apply()

    var gpsLongitude: Double
        get() = java.lang.Double.longBitsToDouble(prefs.getLong(KEY_GPS_LNG, java.lang.Double.doubleToLongBits(106.8456)))
        set(value) = prefs.edit().putLong(KEY_GPS_LNG, java.lang.Double.doubleToLongBits(value)).apply()

    var gpsLocationName: String
        get() = prefs.getString(KEY_GPS_NAME, "Lokasi GPS Terdeteksi") ?: "Lokasi GPS Terdeteksi"
        set(value) = prefs.edit().putString(KEY_GPS_NAME, value).apply()
}
