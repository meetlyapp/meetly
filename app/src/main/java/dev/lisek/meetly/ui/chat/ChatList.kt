package dev.lisek.meetly.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dev.lisek.meetly.backend.data.FetchData.fetchMessageList
import dev.lisek.meetly.backend.data.entity.ChatEntity
import dev.lisek.meetly.ui.profile.Profile

@Composable
fun Messages(nav: NavController, pad: PaddingValues) {
    var chats by remember { mutableStateOf(emptyList<Pair<String, ChatEntity?>>()) }

    LaunchedEffect(null) {
        chats = fetchMessageList()
    }

    Column(Modifier.fillMaxWidth().padding(pad), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth().padding(16.dp, 20.dp), Arrangement.SpaceBetween) {
            Spacer(Modifier.width(32.dp))
            Text("Messages", fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Edit, null)
        }
        LazyColumn {
            items(chats) {
                ChatEntry(nav, it.first, it.second)
            }
        }
    }
}
