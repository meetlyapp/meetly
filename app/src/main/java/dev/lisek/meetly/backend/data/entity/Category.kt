package dev.lisek.meetly.backend.data.entity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

enum class Category(
    val emoji: String,
    val text: String,
    val adult: Boolean = false,
    val string: String = "$emoji $text"
) {
    BAR_GAMES("\uD83C\uDFB1", "Bar games"),
    ALCOHOL("\uD83C\uDF7A", "Bars & pubs", true),
    BOARD_GAMES("\uD83C\uDFB2", "Board games"),
    OUTDOORS("⛺", "Camping & hiking"),
    PARTY("\uD83D\uDC83", "Dance & party"),
    MOVIES("\uD83D\uDCFA", "Movies and series"),
    EVENT("\uD83D\uDCE2", "Organized events"),
    OUTDOOR_GAMES("\uD83C\uDFBE", "Outdoor games"),
    SWIM("\uD83C\uDFD6", "Pool & beach"),
    VIDEO_GAMES("\uD83C\uDFAE", "Video games");

    val color: Color
        @Composable get() = emojiToColor(emoji)
    val textColor: Color
        @Composable get() = textColor(color)

    companion object {
        @Composable
        fun emojiToColor(emoji: String): Color {
            val hash = emoji.codePoints().sum()
            val r = (hash * 31 % 256) / 255f
            val g = (hash * 67 % 256) / 255f
            val b = (hash * 101 % 256) / 255f
            return Color(r, g, b, .1f)
                .compositeOver(MaterialTheme.colorScheme.primary)
        }

        fun textColor(bg: Color): Color {
            return if(bg.luminance() > .5) {
                Color.Black
            } else {
                Color.White
            }
        }
    }
}
