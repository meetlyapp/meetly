package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import dev.lisek.meetly.ui.main.Visibility
import dev.lisek.meetly.ui.profile.Profile
import java.util.Date

/**
 * POJO for a meeting.
 * 
 * @property [id] ID of the meeting.
 * @property [title] title of the meeting.
 * @property [creator] UID of the creator of the meeting.
 * @property [description] description of the meeting.
 * @property [date] date of the meeting.
 * @property [location] coordinates of the meeting.
 * @property [address] address of the meeting.
 * @property [categories] meeting categories.
 * @property [participants] people registered for the meeting.
 * @property [maxParticipants] maximum number of participants.
 * @property [ageRange] allowed age range.
 * @property [visibility] visibility of the meeting.
 *
 * @see Profile.getEntity
 * @see Locatable
 */
class MeetingEntity(
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

/**
 * Wrapper for a meeting entity.
 * Used for better management of created entity.
 * 
 * @see MeetingEntity
 */
class MeetingWrapper {
    var data by mutableStateOf<MeetingEntity?>(null)

    /**
     * Fetches a meeting entity from the database.
     * 
     * @param [id] ID of the meeting.
     */
    fun dataEntity(id: String) {
        FirebaseFirestore
            .getInstance()
            .collection("posts")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObject(MeetingEntity::class.java)
                data?.id = id
            }
    }
}