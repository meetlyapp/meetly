package dev.lisek.meetly.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dev.lisek.meetly.backend.Auth
import dev.lisek.meetly.ui.profile.Profile

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login(Auth(rememberNavController()))
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    Profile.FromUID("example")
}