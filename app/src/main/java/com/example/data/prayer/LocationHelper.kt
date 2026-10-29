package com.example.data.prayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

data class GpsLocationResult(
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val timezoneOffsetHours: Double
)

class LocationHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GpsLocationResult? {
        if (!hasLocationPermission() || locationManager == null) return null

        val lastGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
        val lastNet = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }
        val best = lastGps ?: lastNet

        if (best != null && (System.currentTimeMillis() - best.time) < 1000 * 60 * 60) {
            // Last known is fresh (under 1 hour)
            return buildResult(best)
        }

        // Otherwise request a single fresh update
        return suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    locationManager.removeUpdates(this)
                    if (continuation.isActive) {
                        continuation.resume(buildResult(loc))
                    }
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, Looper.getMainLooper())
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.getMainLooper())
                } else {
                    continuation.resume(best?.let { buildResult(it) })
                }
            } catch (e: Exception) {
                continuation.resume(best?.let { buildResult(it) })
            }

            continuation.invokeOnCancellation {
                try { locationManager.removeUpdates(listener) } catch (_: Exception) {}
            }
        }
    }

    private fun buildResult(loc: Location): GpsLocationResult {
        var name = "Lokasi GPS (${String.format(Locale.US, "%.2f, %.2f", loc.latitude, loc.longitude)})"
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale("id", "ID"))
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val subAdmin = addr.subAdminArea ?: addr.locality ?: addr.adminArea
                    if (!subAdmin.isNullOrBlank()) {
                        name = "GPS: $subAdmin"
                    }
                }
            }
        } catch (_: Exception) {}

        val tzOffsetHours = (TimeZone.getDefault().rawOffset / 3600000.0)

        return GpsLocationResult(
            latitude = loc.latitude,
            longitude = loc.longitude,
            locationName = name,
            timezoneOffsetHours = tzOffsetHours
        )
    }
}
