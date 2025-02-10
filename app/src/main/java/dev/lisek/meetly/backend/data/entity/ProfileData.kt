package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.storage
import dev.lisek.meetly.backend.data.FetchData.db

class ProfileEntity(
    var uid: String = "",
    val name: String = "",
    val surname: String = "",
    val login: String = "",
    val bio: String = "",
    val dob: Timestamp = Timestamp.now(),
    override val location: Map<String, Double> = mapOf("latitude" to .0, "longitude" to .0),
    val friends: List<String> = emptyList(),
    val incomingFriends: List<String> = emptyList(),
    val outgoingFriends: List<String> = emptyList()
) : Locatable {
    val age: Long
        get() = (Timestamp.now().seconds - dob.seconds) / (60 * 60 * 24 * 365)
    val image: String
        @Composable get() {
            var image by remember { mutableStateOf("") }

            LaunchedEffect(uid) {
                Firebase.storage.reference
                    .child("users/$uid/profile.jpeg")
                    .downloadUrl.addOnSuccessListener { uri ->
                        image = uri.toString()
                    }
            }
            return image
        }
    val doc: DocumentReference
        get() = db.collection("users").document(uid)
}

class ProfileWrapper {
    var data by mutableStateOf<ProfileEntity?>(null)

    fun profileEntity(user: String) {
//    LaunchedEffect(user) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(user)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObject(ProfileEntity::class.java)
                data?.uid = user
            }
    }
}
