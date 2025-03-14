package dev.lisek.meetly.backend.data.entity

import androidx.compose.runtime.Composable
import dev.lisek.meetly.backend.Geolocation
import dev.lisek.meetly.ui.profile.Profile

/**
 * Interface for objects that can be located.
 * 
 * @see MeetingEntity
 * @see ProfileEntity
 * @see Geolocation
 */
interface Locatable {
    val location: Map<String, Any>
    val place: Map<String, Double>
        get() = location.mapValues { (it.value as Number).toDouble() }
    val distance: Double
        @Composable get() = Geolocation.distance(Profile.local.place, place)
}
