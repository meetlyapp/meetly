package dev.lisek.meetly.ui.sidebar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dev.lisek.meetly.R
import dev.lisek.meetly.ui.ListCategory
import dev.lisek.meetly.ui.ListEntry
import dev.lisek.meetly.ui.homescreen.HomeScreen
import dev.lisek.meetly.ui.homescreen.MeetingEntry
import dev.lisek.meetly.ui.homescreen.MeetingPanel
import dev.lisek.meetly.ui.profile.Profile
import dev.lisek.meetly.ui.theme.DarkOrange
import dev.lisek.meetly.ui.theme.scriptFamily
import kotlinx.coroutines.launch

/**
 * HomeScreen navigation controller.
 * Exposed for convenience.
 * Might change it later since it's a leak.
 */
object Navigation {
    var nav: NavHostController? = null

    fun navigate(route: String) {
        nav?.navigate(route)
    }
}

/**
 * App navigation overlay.
 * 
 * @param [parent] navigation controller to switch between non-overlayed and overlayed screens.
 * 
 * @see MainActivity
 */
@Composable
fun Overlay(parent: NavController) {
    val nav = rememberNavController()
    Navigation.nav = nav

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.padding(0.dp, 0.dp, 64.dp, 0.dp)
            ) {
                Scaffold(
                    bottomBar = {
                        BottomAppBar(
                            Modifier
                                .height(64.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(50)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            val local = Profile.local
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Row(Modifier.clickable {
                                        nav.navigate("profile/${local.uid}")
                                    }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        local.image, null,
                                        Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape),
                                        painterResource(R.drawable.profile),
                                        painterResource(R.drawable.profile),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Text(
                                        "${Profile.local.name} ${Profile.local.surname}",
                                        Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("(${Profile.local.login})",
                                        color = MaterialTheme.colorScheme.onBackground.copy(.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton({
                                    Firebase.auth.signOut()
                                    parent.navigate("login") { popUpTo("app") { inclusive = true } }
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Default.ExitToApp,
                                        "Logout"
                                    )
                                }
                            }
                        }
                    }
                ) { pad ->
                    Column(
                        Modifier
                            .padding(8.dp)
                            .verticalScroll(ScrollState(0)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "meetly", Modifier,
                            DarkOrange, 32.sp,
                            fontFamily = scriptFamily
                        )
                        HorizontalDivider(Modifier.padding(8.dp))
                        ListCategory("People") {
                            ListEntry("", "Chat", Icons.Default.Email)
                            ListEntry("", "Blocklist", Icons.Default.Clear)
                        }
                        ListCategory("Meetings") {
                            ListEntry("", "Upcoming", Icons.Default.PlayArrow)
                            ListEntry("", "Archive", Icons.Default.Delete)
                        }
                        ListCategory("Premium") {
                            ListEntry("", "Premium", Icons.Default.Star)
                        }
                        ListCategory("Settings & support") {
                            ListEntry("", "Settings", Icons.Default.Settings, { parent.navigate("settings") })
                            ListEntry("", "Support", Icons.Default.AccountCircle)
                            ListEntry("", "About", Icons.Default.Info) {
                                Text("ver. 0.0.1",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                                ) }
                        }
                    }
                    pad
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    content = {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton({ nav.navigate("profile/${Firebase.auth.uid!!}") }) {
                                Icon(Icons.Default.Person, "Profile")
                            }
                            IconButton({ nav.navigate("main") }) {
                                Icon(Icons.AutoMirrored.Default.List, "Meetings")
                            }
                            Button({ nav.navigate("create") }, Modifier.size(64.dp)) {
                                Icon(
                                    Icons.Default.Add,
                                    "Add a new meeting"
                                )
                            }
                            IconButton({}) { Icon(Icons.Default.Face, "Face to face") }
                            IconButton({}) { Icon(Icons.Default.Favorite, "Your meetings") }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(nav, "main") {
                composable("main") { HomeScreen(innerPadding) }
                composable(
                    "meeting/{id}"
                ) { arg ->
                    MeetingPanel(
                        MeetingEntry.getEntity(
                            arg.arguments?.getString("id")!!
                        ) ?: return@composable, false
                    )
                }
                composable(
                    "profile/{uid}"
                ) { arg ->
                    Profile.FromUID(arg.arguments?.getString("uid")!!)
                }
                composable("create") { MeetingPanel() }
            }
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }, Modifier.padding(innerPadding).padding(8.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", Modifier.size(32.dp))
            }
        }
    }
}
