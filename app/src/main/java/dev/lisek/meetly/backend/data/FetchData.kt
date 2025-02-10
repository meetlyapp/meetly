package dev.lisek.meetly.backend.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dev.lisek.meetly.backend.data.entity.DataEntity
import dev.lisek.meetly.ui.main.getCurrentLocation
import kotlinx.coroutines.tasks.await
import java.lang.Math.toRadians
import kotlin.math.cos

object FetchData {
    @SuppressLint("StaticFieldLeak")
    val db = FirebaseFirestore.getInstance()

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
            return query.get("location")!! as Map<String, Double>
        }
    }

    suspend fun fetchMeetings(context: Context, radius: Int): List<DataEntity> {
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
            val meet = it.toObject(DataEntity::class.java)
            meet?.id = it.id
            meet ?: DataEntity()
        }
        return meets
    }
}