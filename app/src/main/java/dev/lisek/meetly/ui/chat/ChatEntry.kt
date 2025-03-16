package dev.lisek.meetly.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.data.entity.ChatEntity
import dev.lisek.meetly.ui.profile.Profile
import java.text.SimpleDateFormat

fun date(date: Timestamp): String {
    var seconds = Timestamp.now().seconds - date.seconds

    return if (seconds < 60)
        "now"
    else if (seconds / 60 < 60)
        "${seconds / 60}m"
    else if (seconds / (60 * 60) < 24)
        "${seconds / (60 * 60)}h"
    else if (seconds / (60 * 60 * 24) <= 7)
        "${seconds / (60 * 60 * 24)}d"
    else if (seconds / (60 * 60 * 24 * 7) <= 4)
        "${seconds / (60 * 60 * 24 * 7)}w"
    else
        "${SimpleDateFormat("d/M/yyyy").format(date.toDate())}"
}

@Composable
fun ChatEntry(content: ChatEntity?) {
    val messageDate = date(content?.lastMessage["date"] as Timestamp)

    Row(Modifier
        .fillMaxWidth()
        .clickable {}
        .padding(16.dp, 8.dp)
    ) {
        AsyncImage(
            if (content.chatMembers.isEmpty()) "" else content.chatMembers[0]?.image,
            "${content.chatName}'s profile picture",
            Modifier.size(56.dp).clip(CircleShape),
            painterResource(R.drawable.profile),
            painterResource(R.drawable.profile)
        )
        Column(Modifier.padding(8.dp, 4.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(
                    content.chatName,
                    fontWeight = FontWeight.Bold
                )
                Text(messageDate)
            }
            Text(
                "${
                    if ((content.lastMessage["sender"] as String).isEmpty())
                        return
                    else if (content.lastMessage["sender"] == Firebase.auth.uid)
                        "You"
                    else
                        Profile.getEntity(content.lastMessage["sender"] as String)?.name
                }: ${content.lastMessage["message"] as String}",
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}
