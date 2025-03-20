package dev.lisek.meetly.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.lisek.meetly.backend.auth.Auth
import dev.lisek.meetly.ui.login.Login
import dev.lisek.meetly.ui.profile.Profile


@Composable
fun LoginPreview(navController: NavController) {
    Login(Auth(navController), PaddingValues(), navController)
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    Profile.FromUID("example")
}