package dev.lisek.meetly.ui.navigation

import ResetPasswordScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.lisek.meetly.backend.auth.Auth
import dev.lisek.meetly.ui.login.Login
import dev.lisek.meetly.ui.Overlay
import dev.lisek.meetly.ui.Settings
import dev.lisek.meetly.ui.homescreen.HomeScreen

@Composable
fun MeetlyNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    auth: Auth,
    pad:PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = if (auth.isLogged()) "app" else "login",
    ) {
        composable("resetScreen") {
            ResetPasswordScreen(modifier, navController)
        }
        composable("login") {
            Login(auth, pad, navController)
        }
        composable("app") {
            Overlay(navController)
        }
        composable("settings") {
            Settings(pad)
        }
        composable("homeScreen"){
            HomeScreen(pad)

        }
    }
}
