package dev.lisek.meetly.backend.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
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

//    val color: Color
//        @Composable get() = emojiToColor(emoji)
//    val textColor: Color
//        @Composable get() = textColor(color)

    /**
     * Color map the category from its emoji.
     * 
     * @param [emoji] emoji to convert.
     * @return background color.
     */
    @Composable
    fun emojiToColor(emoji: String): Color {
        val hash = emoji.codePoints().sum()
        val r = (hash * 31 % 256) / 255f
        val g = (hash * 67 % 256) / 255f
        val b = (hash * 101 % 256) / 255f
        return Color(r, g, b, .1f)
            .compositeOver(MaterialTheme.colorScheme.primary)
    }

    /**
     * Determine the text color based on the background color.
     * 
     * @param [bg] background color.
     * @return text color.
     */
    fun textColor(bg: Color): Color {
        return if(bg.luminance() > .5) {
            Color.Black
        } else {
            Color.White
        }
    }
}
