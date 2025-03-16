package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import dev.lisek.meetly.ui.profile.Profile

class ChatEntity(
    val name: String? = null,
    val participants: List<String> = emptyList(),
    val lastMessage: Map<String, Any> = mapOf(
        "sender" to "",
        "message" to "",
        "date" to Timestamp.now()
    )
) {
    val chatName: String
        @Composable get() {
            return if (name?.isEmpty() != false) {
                val members = chatMembers.map {
                    "${it?.name} ${it?.surname}"
                }.joinToString(", ")
                if (members.isEmpty()) "Empty chat" else members
            }
            else name
        }
    val chatMembers: List<ProfileEntity?>
        @Composable get() = participants.filter {
            it != Firebase.auth.uid
        }.map { Profile.getEntity(it) }
}
