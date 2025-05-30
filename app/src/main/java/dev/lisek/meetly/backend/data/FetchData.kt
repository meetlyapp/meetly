package dev.lisek.meetly.backend.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dev.lisek.meetly.backend.data.entity.MeetingEntity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.lang.Math.toRadians
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.cos

/**
 * Collection of data fetching methods.
 */
object FetchData {
    @SuppressLint("StaticFieldLeak")
    val db = FirebaseFirestore.getInstance()

    suspend fun fetchLocation(context: Context, geo: Boolean = false): Map<String, Double> {
        return if (geo) {
            val location = getCurrentLocation(context)
            location?.let {
                mapOf("latitude" to it.latitude, "longitude" to it.longitude)
            } ?: throw IllegalStateException("Location not found")

        } else {
            val db = Firebase.firestore
            val uid = FirebaseAuth.getInstance().uid ?: throw IllegalStateException("User not logged in")
            val doc = db.collection("users").document(uid).get().await()
            val locationMap = doc.get("location") as? Map<String, Any> ?: emptyMap()
            locationMap.mapValues { (_, value) -> (value as Number).toDouble() }
        }
    }

    @Suppress("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            throw SecurityException("Location permission not granted")
        }

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    /**
     * Fetch meetings within a given radius.
     * 
     * @param [context] activity context.
     * @param [radius] radius (in kilometers).
     */
    suspend fun fetchMeetings(
        context: Context,
        radius: Int,
        categories: Set<String>
    ): List<MeetingEntity> {
        val location = fetchLocation(context)
        val lat = location["latitude"] ?: .0
        val lon = location["longitude"] ?: .0

        var query = db.collection("posts")
            .whereLessThan("location.latitude", lat + radius/111.0)
            .whereLessThan("location.longitude", lon + radius/(111.0 / cos(toRadians(lat))))
            .whereGreaterThan("location.latitude", lat - radius/111.0)
            .whereGreaterThan("location.longitude", lon - radius/(111.0 / cos(toRadians(lat))))

        if (!categories.isEmpty()) {
            query = query.whereArrayContainsAny("categories", categories.toList())
        }

        val meetings = query.get().await()

        val meets = meetings.documents.map {
            val meet = it.toObject(MeetingEntity::class.java)
            meet?.id = it.id
            meet ?: MeetingEntity()
        }
        return meets
    }
}