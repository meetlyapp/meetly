package dev.lisek.meetly.backend.geo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dev.lisek.meetly.ui.profile.Profile
import java.io.IOException
import java.lang.Math.toRadians
import java.util.Locale
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
        geo: Boolean = true,
        onLocationRetrieved: (LatLng?) -> Unit
    ) {
        val context = LocalContext.current
        val localProfile = Profile.local
        if (geo) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!permission) {
                getPermission {  }
                onLocationRetrieved(LatLng(
                    localProfile.place["latitude"]!!,
                    localProfile.place["longitude"]!!
                ))
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    onLocationRetrieved(location?.let { LatLng(it.latitude, it.longitude) })
                }
                .addOnFailureListener {
                    onLocationRetrieved(LatLng(
                        localProfile.place["latitude"]!!,
                        localProfile.place["longitude"]!!
                    ))
                }
        } else {
            onLocationRetrieved(LatLng(
                localProfile.place["latitude"]!!,
                localProfile.place["longitude"]!!
            ))
        }
    }

    @Composable
    fun getPermission(onPermissionGranted: (LatLng?) -> Unit): Boolean {
        val context = LocalContext.current
        var permission by remember { mutableStateOf(false) }
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            permission = isGranted
            if (isGranted) {
                getCurrentLocation(context) { onPermissionGranted(it) }
            }
        }

        // Check permission status
        LaunchedEffect(Unit) {
            permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (permission) {
                getCurrentLocation(context) { onPermissionGranted(it) }
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        return permission
    }

    /**
     * Get user's current location.
     *
     * @param [context] activity context.
     * @param [onLocationRetrieved] Callback to return the location.
     */
    fun getCurrentLocation(
        context: Context,
        onLocationRetrieved: (LatLng?) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val permission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permission) {
            onLocationRetrieved(null)
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                onLocationRetrieved(location?.let { LatLng(it.latitude, it.longitude) })
            }
            .addOnFailureListener {
                onLocationRetrieved(null)
            }
    }

    /**
     * Get user's address from coordinates.
     *
     * @param [context] activity context.
     * @param [latLng] user coordinates.
     */
    fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
        } catch (e: IOException) {
            "Error: ${e.localizedMessage}"
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
