package dev.lisek.meetly.backend.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dev.lisek.meetly.backend.data.entity.MeetingEntity
import dev.lisek.meetly.ui.main.getCurrentLocation
import kotlinx.coroutines.tasks.await
import java.lang.Math.toRadians
import kotlin.math.cos

/**
 * Collection of data fetching methods.
 */
object FetchData {
    @SuppressLint("StaticFieldLeak")
    val db = FirebaseFirestore.getInstance()

    /**
     * Fetch user's location.
     * 
     * @param [context] activity context.
     * @param [geo] whether to use geolocation or not.
     */
    private suspend fun fetchLocation(context: Context, geo: Boolean = false): Map<String, Double> {
        if (geo) {  // TODO: fix!
            var loc = mutableMapOf<String, Double>()
            getCurrentLocation(context) {
                loc["latitude"] = it?.latitude ?: .0
                loc["longitude"] = it?.longitude ?: .0
            }
            return loc
        } else {
            val query = db.collection("users")
                .document(Firebase.auth.uid!!)
                .get().await()
            return (query.get("location")!! as Map<String, Any>)
                .mapValues { it.value as Double }
        }
    }

    /**
     * Fetch meetings within a given radius.
     * 
     * @param [context] activity context.
     * @param [radius] radius (in kilometers).
     */
    suspend fun fetchMeetings(context: Context, radius: Int): List<MeetingEntity> {
        val location = fetchLocation(context)
        val lat = location["latitude"] ?: .0
        val lon = location["longitude"] ?: .0

        val meetings = db.collection("posts")
            .whereLessThan("location.latitude", lat + radius/111.0)
            .whereLessThan("location.longitude", lon + radius/(111.0 / cos(toRadians(lat))))
            .whereGreaterThan("location.latitude", lat - radius/111.0)
            .whereGreaterThan("location.longitude", lon - radius/(111.0 / cos(toRadians(lat))))
            .get().await()

        val meets = meetings.documents.map {
            val meet = it.toObject(MeetingEntity::class.java)
            meet?.id = it.id
            meet ?: MeetingEntity()
        }
        return meets
    }
}