package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.storage
import com.google.type.LatLng
import dev.lisek.meetly.backend.Geolocation
import dev.lisek.meetly.ui.main.Visibility
import dev.lisek.meetly.ui.profile.Profile
import java.util.Date

class DataEntity(
    var id: String? = null,
    val title: String? = null,
    val creator: String? = null,
    val description: String? = null,
    val date: Date = Date(),
    override val location: Map<String, Double> = mapOf("latitude" to .0, "longitude" to .0),
    val address: String = "Unknown location",
    val categories: List<String> = emptyList(),
    val participants: List<String> = emptyList(),
    val maxParticipants: Int = 0,
    val ageRange: Map<String, Int>? = null,
    val visibility: String = "PRIVATE"
) : Locatable {
    val author: ProfileEntity
        @Composable get() = Profile.getEntity(creator ?: "none") ?: ProfileEntity()
    val category: List<Category>
        get() = categories.map { Category.valueOf(it) }
    val visible: Visibility
        get() = Visibility.valueOf(visibility)
    val image: String
        @Composable get() {
            var image by remember { mutableStateOf("") }

            LaunchedEffect(id) {
                Firebase.storage.reference
                    .child("posts/$id/photo.jpeg")
                    .downloadUrl.addOnSuccessListener { uri ->
                        image = uri.toString()
                    }
            }
            return image
        }
}

class DataWrapper {
    var data by mutableStateOf<DataEntity?>(null)

    fun dataEntity(id: String) {
        FirebaseFirestore
            .getInstance()
            .collection("posts")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObject(DataEntity::class.java)
                data?.id = id
            }
    }
}