package dev.lisek.meetly.ui.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import dev.lisek.meetly.backend.data.FetchData
import dev.lisek.meetly.backend.category.Category
import dev.lisek.meetly.backend.geo.Geolocation.UserLocation
import dev.lisek.meetly.backend.geo.Geolocation.getAddressFromLatLng
import dev.lisek.meetly.backend.meeting.MeetingEntity

/**
 * HomeScreen screen of the app - collective meetings.
 * 
 * @property [pad] padding values
 * 
 * @see [MeetingEntry]
 */
@Composable
fun HomeScreen(pad: PaddingValues = PaddingValues(0.dp)) {
    var radius by remember { mutableIntStateOf(5) }
    var radiusDropdown by remember { mutableStateOf(false) }
    var refresher by remember { mutableStateOf(false) }
    var posts by remember { mutableStateOf(emptyList<MeetingEntity>()) }
    var location by remember { mutableStateOf("") }
    val context = LocalContext.current

    UserLocation() {
        location = getAddressFromLatLng(context, it ?: LatLng(.0, .0)).split(",")[0]
    }

    LaunchedEffect(refresher) {
        posts = FetchData.fetchMeetings(context, radius)
    }

    Column(modifier = Modifier.fillMaxSize().padding(pad)) {
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(96.dp))
            Row {
                Icon(Icons.Default.LocationOn, contentDescription = "Location")
                Text(location)
            }
            Column {
                TextButton(
                    { radiusDropdown = true },
                    Modifier.width(96.dp)
                ) { Text("+ $radius km") }
                DropdownMenu(radiusDropdown, { radiusDropdown = false }) {
                    for (distance in listOf(1, 2, 5, 10, 15, 30, 50, 100, 300, 500))
                        DropdownMenuItem({ Text(distance.toString()) }, {
                            radius = distance
                            radiusDropdown = false
                            refresher = !refresher
                        })
                }
            }
        }
        OutlinedTextField("", {}, Modifier
            .fillMaxWidth()
            .padding(16.dp),
            shape = RoundedCornerShape(50),
            placeholder = {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("What are you up to?")
                    Icon(Icons.Default.Search, null)
                }
            },
        )
        LazyColumn(Modifier.weight(1f)) {
            item {
                LazyRow {
                    items(Category.entries) {
                        Button({},
                            Modifier.padding(4.dp),
                            colors = ButtonDefaults.buttonColors(it.color, it.textColor)
                        ) { Text(it.string) }
                    }
                }
            }
            items(posts) {
                MeetingEntry.FromEntity(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    HomeScreen()
}
