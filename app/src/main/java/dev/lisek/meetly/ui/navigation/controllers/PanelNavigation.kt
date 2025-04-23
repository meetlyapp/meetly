package dev.lisek.meetly.ui.navigation.controllers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.lisek.meetly.ui.homescreen.HomeScreen
import dev.lisek.meetly.ui.homescreen.MeetingEntry
import dev.lisek.meetly.ui.homescreen.MeetingPanel
import dev.lisek.meetly.ui.profile.Profile

@Composable
fun PanelNavigation(
    navController: NavHostController,
    pad: PaddingValues
) {
    NavHost(navController, "main") {
        composable("main") { HomeScreen(pad) }
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
}