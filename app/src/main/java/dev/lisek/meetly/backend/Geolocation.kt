package dev.lisek.meetly.backend

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import dev.lisek.meetly.ui.profile.Profile
import java.lang.Math.toRadians
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Collection of geolocation methods.
 */
object Geolocation {

    /**
     * Fetch user's location.
     * 
     * @param [geo] whether to use geolocation or not.
     * @param [fusedLocationClient] fused location client.
     * @param [onLocationRetrieved] callback to pass the location.
     */
    @Composable
    fun UserLocation(
        geo: Boolean,
        fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
        onLocationRetrieved: (LatLng?) -> Unit
    ) {
        val context = LocalContext.current
        if (geo) {
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!permission) {
                onLocationRetrieved(LatLng(
                    Profile.local.place["latitude"]!!,
                    Profile.local.place["longitude"]!!
                ))
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    onLocationRetrieved(location?.let { LatLng(it.latitude, it.longitude) })
                }
                .addOnFailureListener {
                    onLocationRetrieved(LatLng(.0, .0))
                }
        } else {
            onLocationRetrieved(LatLng(
                Profile.local.place["latitude"]!!,
                Profile.local.place["longitude"]!!
            ))
        }
    }

    /**
     * Calculate distance between two locations.
     * 
     * @param [origin] origin location.
     * @param [destination] destination location.
     */
    fun distance(origin: Map<String, Double>, destination: Map<String, Double>): Double {
        val deltaLat = toRadians(destination["latitude"]!! - origin["latitude"]!!)
        val deltaLon = toRadians(destination["longitude"]!! - origin["longitude"]!!)

        val oLat = toRadians(origin["latitude"]!!)
        val dLat = toRadians(destination["latitude"]!!)

        val a = sin(deltaLat / 2).pow(2) +
                sin(deltaLon / 2).pow(2) *
                cos(oLat) * cos(dLat)
        val rad = 6371
        val c = 2 * asin(sqrt(a))
        return 1000 * rad * c
    }
}
