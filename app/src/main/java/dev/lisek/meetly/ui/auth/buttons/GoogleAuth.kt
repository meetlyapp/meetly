package dev.lisek.meetly.ui.auth.buttons

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.auth.google.GoogleSignInClient
import kotlinx.coroutines.launch

@Composable
fun GoogleAuth(context: Context, isSignedIn: Boolean, navController: NavController) {
    val googleSignInClient = GoogleSignInClient(
        context,
        isSignedIn
    )
    val coroutineScope = rememberCoroutineScope()

    Button(onClick = {
        coroutineScope.launch {
            googleSignInClient.signIn()
            navController.navigate("homeScreen")
        }
    }) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Google login",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text("Login with Google")
    }
}
