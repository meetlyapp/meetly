package dev.lisek.meetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.lisek.meetly.ui.PasswordReset
import dev.lisek.meetly.ui.PasswordReset

@Composable
fun MeetlyNavigation(
    modifier: Modifier = Modifier,

) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        builder = {
            composable("PasswordReset") {
                PasswordReset(navController)
            }
        }
    )

}