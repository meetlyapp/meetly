package dev.lisek.meetly.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.meeting.MeetingEntity
import dev.lisek.meetly.backend.meeting.MeetingWrapper
import dev.lisek.meetly.ui.sidebar.Navigation
import java.text.SimpleDateFormat

/**
 * Small, *thumbnail* representation of a meeting.
 */
object MeetingEntry {

    /**
     * Get a meeting entity by its ID.
     * 
     * @param [id] ID of the meeting.
     */
    @Composable
    fun getEntity(id: String): MeetingEntity? {
        val dw = remember { MeetingWrapper() }

        LaunchedEffect(id) {
            dw.dataEntity(id)
        }

        return dw.data
    }

    /**
     * Get a meeting thumbnail by its ID.
     * 
     * @param [id] ID of the meeting.
     */
    @Composable
    fun FromID(id: String) {
        FromEntity(getEntity(id))
    }

    /**
     * Get a meeting thumbnail from a meeting entity.
     * 
     * @param [data] Meeting entity.
     */
    @Composable
    fun FromEntity(data: MeetingEntity?) {
        data?.let {
            Column(
                Modifier
                    .padding(16.dp, 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    ), RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(onClick = {
                        Navigation.navigate("meeting/${it.id}")
                    }),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                ) {
                    AsyncImage(
                        model = it.image,
                        contentDescription = "Meeting image",
                        placeholder = painterResource(R.drawable.landscape),
                        error = painterResource(R.drawable.landscape),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .fillMaxWidth()
                            .aspectRatio(2f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                )
                            )
                    )
                }
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier.weight(1f),
                            Arrangement.Start,
                            Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, "Location", Modifier.size(16.dp))
                            Text("${it.distance.toInt()}m", fontSize = 12.sp)
                        }
                        Text(
                            "${it.author.name} ${it.author.surname}",
                            Modifier.weight(1f),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Row(Modifier.weight(1f), Arrangement.End, Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, "Participants", Modifier.size(16.dp))
                            Text("${it.participants.size}${
                                if (it.maxParticipants == 0) "" else "/${it.maxParticipants}"
                            }", fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(it.title ?: "Loading...", fontWeight = FontWeight.Bold)
                    Text(it.address, fontSize = 12.sp)
                    Text(
                        SimpleDateFormat("MMM d, yyyy - HH:mm").format(it.date),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
