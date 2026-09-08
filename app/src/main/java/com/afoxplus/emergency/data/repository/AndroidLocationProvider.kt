package com.afoxplus.emergency.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.repository.LocationProvider

/**
 * Captures the device's last known location using the platform [LocationManager],
 * avoiding any dependency on Google Play Services location APIs. Returns `null` when
 * location permissions are missing, GPS/network providers are disabled, or no last
 * known location is available, so the emergency alert flow is never blocked by it.
 */
class AndroidLocationProvider(
    private val context: Context
) : LocationProvider {

    override fun getCurrentLocation(): Coordinates? {
        if (!hasLocationPermission()) return null

        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return null

            locationManager.allProviders
                .mapNotNull { provider ->
                    try {
                        locationManager.getLastKnownLocation(provider)
                    } catch (_: SecurityException) {
                        null
                    }
                }
                .maxByOrNull { it.time }
                ?.let { location -> Coordinates(location.latitude, location.longitude) }
        } catch (_: Exception) {
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocation == PackageManager.PERMISSION_GRANTED ||
            coarseLocation == PackageManager.PERMISSION_GRANTED
    }
}
