package dev.lisek.meetly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dev.lisek.meetly.backend.auth.Auth
import dev.lisek.meetly.backend.data.FetchData
import dev.lisek.meetly.ui.RequestNotificationPermission
import dev.lisek.meetly.ui.navigation.controllers.MeetlyNavigation
import dev.lisek.meetly.ui.showNotification
import dev.lisek.meetly.ui.theme.MeetlyTheme
import kotlin.random.Random

/**
 * HomeScreen activity of the app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }
        enableEdgeToEdge()
        setContent { Root() }
    }

    /**
     * Root composable of the app.
     */
    @Composable
    fun Root() {
        val nav = rememberNavController()
        val auth = Auth(nav)
        val context = LocalContext.current

        RequestNotificationPermission()

        LaunchedEffect(Unit) {
            if (Firebase.auth.uid.isNullOrBlank())
                return@LaunchedEffect
            showNotification(
                context,
                "New meetings",
                "Buckle up! There are ${
                    FetchData.fetchMeetings(context, Random.nextInt(5, 15), emptySet()).size
                } meetings near you. \uD83D\uDC6F"
            )
        }

        MeetlyTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MeetlyNavigation(
                    nav,
                    auth,
                    innerPadding
                )
            }
        }
    }
}
