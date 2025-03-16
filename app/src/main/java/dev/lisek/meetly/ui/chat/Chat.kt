package dev.lisek.meetly.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import dev.lisek.meetly.R
import dev.lisek.meetly.ui.theme.DarkOrange

@Composable
fun Chat(id: String) {
    val now = Timestamp.now().seconds
    val now_ns = Timestamp.now().nanoseconds
    var message by remember { mutableStateOf("") }
    var messages = remember {
        mutableStateMapOf(
            1 to listOf("uid_1", "Hello", Timestamp(now - 94682, now_ns)),
            2 to listOf("uid_1", "How are you?", Timestamp(now - 94675, now_ns)),
            3 to listOf("uid_2", "hi", Timestamp(now - 93843, now_ns)),
            4 to listOf("uid_2", "pretty good, just chilling", Timestamp(now - 93832, now_ns)),
            5 to listOf("uid_2", "wbu?", Timestamp(now - 93829, now_ns)),
            6 to listOf("uid_1", "Pretty good, thanks for asking :)", Timestamp(now - 91430, now_ns)),
            7 to listOf("uid_1", "I found a nice party nearby", Timestamp(now - 85992, now_ns)),
            8 to listOf("uid_1", "been wondering if you'd wanna go? I'm so bored and I'd really use that. I'd be super glad to go with you", Timestamp(now - 85964, now_ns)),
            9 to listOf("uid_2", "I mean, every opportunity to get out of bed is a good one", Timestamp(now - 3251, now_ns)),
            10 to listOf("uid_2", "So yeah, sure! Send me a link?", Timestamp(now - 423, now_ns)),
            11 to listOf("uid_1", "Yeah sure! one sec tho, I'm making dinner rn", Timestamp(now - 164, now_ns)),
            12 to listOf("uid_2", "mkayyy :>", Timestamp(now - 143, now_ns)),
            13 to listOf("uid_2", "what good are u cooking?", Timestamp(now - 135, now_ns)),
            14 to listOf("uid_1", "shrimp fry pasta \uD83C\uDF64", Timestamp(now - 71, now_ns)),
            15 to listOf("uid_2", "deliciousss \uD83D\uDE0B", Timestamp(now - 59, now_ns))
        )
    }
    Scaffold(
        Modifier.fillMaxSize(), {
            Row(
                Modifier.padding(8.dp).statusBarsPadding().fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton({
                        // to be implemented after externing main navController
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Go back")
                    }
                    Image(
                        painterResource(R.drawable.profile),
                        "Profile picture",
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        "Cool Friend",
                        Modifier.padding(16.dp, 0.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton({}) {
                    Icon(Icons.Default.Menu, "Chat options")
                }
            }
        }, {
            Row(
                Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp).imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton({}) {
                    Icon(Icons.Default.Add, "Add a file")
                }
                OutlinedTextField(
                    message,
                    { message = it },
                    Modifier.fillMaxWidth(),
                    placeholder = { Text("Message...") },
                    shape = RoundedCornerShape(50)
                )
            }
        }
    ) { pad ->
        Column(Modifier
            .padding(pad)
            .fillMaxSize()
            .verticalScroll(ScrollState(Int.MAX_VALUE)),
            Arrangement.Bottom
        ) {
            for (msg in messages) {
                var mod = Modifier
                    .padding(8.dp, 2.dp)

                if (msg.value[0] == "uid_1") {
                    if (msg.value[0] != messages[msg.key + 1]?.get(0)) {
                        mod = mod.padding(bottom = 8.dp).clip(RoundedCornerShape(bottomEnd = 16.dp))
                    }
                    if (msg.value[0] != messages[msg.key - 1]?.get(0)) {
                        mod = mod.padding(top = 8.dp).clip(RoundedCornerShape(topEnd = 16.dp))
                    }
                    mod = mod.clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .background(DarkOrange)
                        .padding(12.dp, 8.dp)
                        .widthIn(0.dp, 256.dp)

                    Row(
                        Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            date(msg.value[2] as Timestamp),
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            fontSize = 10.sp
                        )
                        Text(msg.value[1] as String, mod)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (msg.value[0] != messages[msg.key + 1]?.get(0)) {
                            mod = mod.padding(bottom = 8.dp).clip(RoundedCornerShape(bottomStart = 16.dp))
                            Image(
                                painterResource(R.drawable.profile),
                                "Profile picture",
                                Modifier
                                    .padding(start = 8.dp, bottom = 12.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Bottom)
                            )
                        } else {
                            mod = mod.padding(start = 40.dp)
                        }
                        if (msg.value[0] != messages[msg.key - 1]?.get(0)) {
                            mod = mod.padding(top = 8.dp).clip(RoundedCornerShape(topStart = 16.dp))
                        }

                        mod = mod.clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(12.dp, 8.dp)
                            .widthIn(0.dp, 216.dp)

                        Text(msg.value[1] as String, mod)
                        Text(
                            date(msg.value[2] as Timestamp),
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
