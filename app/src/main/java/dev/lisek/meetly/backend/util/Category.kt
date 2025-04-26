package dev.lisek.meetly.backend.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Categories of events.
 * 
 * @property [emoji] emoji representation.
 * @property [text] category name.
 * @property [adult] true if category is for adults only.
 */
enum class Category(
    val icon: ImageVector,
    val text: String,
    val adult: Boolean = false
) {
    BAR_GAMES(Icons.Default.Radar, "Bar games"),
    ALCOHOL(Icons.Default.LocalBar, "Bars & pubs", true),
    BOARD_GAMES(Icons.Default.Dashboard, "Board games"),
    OUTDOORS(Icons.Default.Landscape, "Camping & hiking"),
    PARTY(Icons.Default.Celebration, "Dance & party"),
    MOVIES(Icons.Default.Movie, "Movies and series"),
    EVENT(Icons.Default.Event, "Organized events"),
    OUTDOOR_GAMES(Icons.Default.SportsBasketball, "Outdoor games"),
    SWIM(Icons.Default.BeachAccess, "Pool & beach"),
    VIDEO_GAMES(Icons.Default.Gamepad, "Video games");
}
