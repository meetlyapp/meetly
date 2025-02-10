package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import dev.lisek.meetly.backend.Geolocation
import dev.lisek.meetly.ui.profile.Profile

interface Locatable {
    val location: Map<String, Double>
    val distance: Double
        @Composable get() = Geolocation.distance(Profile.local.location, location)
}
