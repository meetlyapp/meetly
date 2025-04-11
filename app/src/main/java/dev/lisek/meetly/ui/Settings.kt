package dev.lisek.meetly.ui

import android.os.Build
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lisek.meetly.ui.theme.DarkOrange
import dev.lisek.meetly.ui.theme.LightOrange

/**
 * Category wrapper for settings entries.
 * 
 * @param [title] title of the category.
 * @param [horizontal] whether the category should be displayed horizontally.
 * @param [content] content of the category.
 */
@Composable
fun ListCategory(title: String, horizontal: Boolean = false, content: @Composable (() -> Unit)) {
    val modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)
    Column {
        Text(title, Modifier.padding(16.dp, 8.dp), fontSize = 12.sp)
        if (horizontal)
            Row(modifier.fillMaxWidth()) { content.invoke() }
        else
            Column(modifier.offset(y = 1.dp)) { content.invoke() }
        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Settings entry.
 * 
 * @param [id] id of the entry.
 * @param [text] text of the entry.
 * @param [icon] icon of the entry.
 * @param [onClick] callback to run when the entry is clicked.
 * @param [content] content of the entry.
 */
@Composable
fun ListEntry(
    id: String,
    text: String,
    icon: ImageVector? = null,
    onClick: (() -> Unit) = {},
    content: @Composable (() -> Unit) = {}
) {
    Column {
        Row(
            Modifier
                .height(48.dp)
                .fillMaxWidth()
                .clickable(onClick = onClick),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null)
                    Icon(icon, text, Modifier.padding(start = 16.dp))
                Text(text, Modifier.padding(16.dp, 0.dp))
            }
            Box(Modifier.padding(end = 16.dp)) {
                content.invoke()
            }
        }
        Spacer(Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background))
    }
}

/**
 * Settings Entry with a switch.
 * 
 * @param [id] id of the entry.
 * @param [text] text of the entry.
 */
@Composable
fun SwitchEntry(id: String, text: String) {
    var bool by remember { mutableStateOf(false) }  // fetch initial value from storage
    ListEntry(id, text) {
        Switch(bool, {
            bool = it
            // store settings in storage,
            // possibly sync online too
        })
    }
}

class ImageSwitch(default: String) {
    var choice = mutableStateOf(default)
    @Composable
    fun color(id: String): Color {
        return if (id == choice.value) {
            LightOrange
        } else {
            MaterialTheme.colorScheme.onBackground
        }
    }

    /**
     * Theme entry displaying its color scheme.
     * 
     * @param [id] id of the entry.
     * @param [text] text of the entry.
     * @param [color] color of the theme.
     */
    @Composable
    fun ThemeEntry(
        id: String,
        text: String,
        color: Color
    ) {
        Column(
            Modifier.padding(0.dp, 8.dp).clip(RoundedCornerShape(16.dp)).clickable { choice.value = id }.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, color(id), CircleShape)
                    .background(color)
            )
            Spacer(Modifier.height(8.dp))
            Text(text, color = color(id))
        }
    }

    /**
     * Mock phone entry representing a layout.
     * 
     * @param [id] id of the entry.
     * @param [text] text of the entry.
     * @param [fill] width of the entry.
     * @param [padding] padding of the entry.
     * @param [content] content of the entry.
     */
    @Composable
    fun PhoneEntry(
        id: String,
        text: String,
        fill: Float,
        padding: Dp = 8.dp,
        content: @Composable (() -> Unit)
    ) {
        Column(
            Modifier.fillMaxWidth(fill).clickable { choice.value = id }.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.width(96.dp).height(160.dp)
                    .border(2.dp, color(id), RoundedCornerShape(16.dp))
                    .padding(padding)
            ) {
                content.invoke()
            }
            Spacer(Modifier.height(8.dp))
            Text(text, color = color(id))
        }
    }
}

/**
 * Settings panel.
 * 
 * @param [pad] padding of the panel.
 */
@Composable
fun Settings(pad: PaddingValues) {
    val meetings = "panel_meetings"
    val ftf = "panel_facetoface"
    val photo = "profile_photo"
    val bio = "profile_bio"

    val mainPanel = ImageSwitch(meetings)
    val profileView = ImageSwitch(photo)

    Column(Modifier.padding(pad)) {
        Row(
            Modifier
                .padding(8.dp, 8.dp, 8.dp, 0.dp)
                .fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            IconButton({}) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, null)
            }
            Text("Settings", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(48.dp))
        }
        Column(Modifier.padding(16.dp, 8.dp).verticalScroll(ScrollState(0))) {
            ListCategory("HomeScreen panel", true) {
                mainPanel.PhoneEntry(meetings, "Meetings", 0.5f) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, null, Modifier.size(8.dp), tint = mainPanel.color(meetings))
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(32.dp).height(2.dp)
                                    .background(mainPanel.color(meetings))
                            )
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(8.dp).height(2.dp)
                                    .background(mainPanel.color(meetings))
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(6f)
                                .border(1.dp, mainPanel.color(meetings), RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(1.6f)
                                .border(1.dp, mainPanel.color(meetings), RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(1.6f)
                                .border(1.dp, mainPanel.color(meetings), RoundedCornerShape(16.dp))
                        )
                    }
                }
                mainPanel.PhoneEntry(ftf, "Face to face", 1f) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, null, Modifier.size(8.dp), tint = mainPanel.color(ftf))
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(32.dp).height(2.dp)
                                    .background(mainPanel.color(ftf))
                            )
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(8.dp).height(2.dp)
                                    .background(mainPanel.color(ftf))
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(6f)
                                .border(1.dp, mainPanel.color(ftf), RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(Modifier.align(Alignment.CenterHorizontally)) {
                            Box(Modifier.rotate(10f).offset(x = 8.dp)) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.dp,
                                            mainPanel.color(ftf).copy(0.25f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                )
                            }
                            Box(Modifier.rotate(5f).offset(x = 4.dp)) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.dp,
                                            mainPanel.color(ftf).copy(0.5f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                )
                            }
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.dp,
                                        mainPanel.color(ftf).copy(0.75f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            )
                            Box(Modifier.rotate(-5f).offset(x = (-4).dp)) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.dp,
                                            mainPanel.color(ftf),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                ) {
                                    Icon(Icons.Default.Person, null, Modifier.fillMaxSize(), tint = mainPanel.color(ftf))
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier.width(64.dp).height(2.dp)
                                .background(mainPanel.color(ftf))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(72.dp).height(2.dp)
                                .background(mainPanel.color(ftf))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(56.dp).height(2.dp)
                                .background(mainPanel.color(ftf))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(48.dp).height(2.dp)
                                .background(mainPanel.color(ftf))
                        )
                    }
                }
            }
            ListCategory("Default profile view", true) {
                profileView.PhoneEntry(photo, "Photo", 0.5f, 0.dp) {
                    Column(Modifier.fillMaxSize().offset(y = (-16).dp), Arrangement.Center) {
                        Icon(Icons.Default.Person, null, Modifier.fillMaxWidth().aspectRatio(1f), tint = profileView.color(photo))
                    }
                    Column(Modifier.fillMaxSize(), Arrangement.Bottom) {
                        Row(Modifier.padding(8.dp, 0.dp, 8.dp, 4.dp)) {
                            Box(
                                Modifier.width(48.dp).height(4.dp)
                                    .background(profileView.color(photo))
                            )
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(8.dp).height(4.dp)
                                    .background(profileView.color(photo))
                            )
                        }
                        Box(Modifier.padding(8.dp, 0.dp)) {
                            Box(
                                Modifier.width(32.dp).height(2.dp)
                                    .background(profileView.color(photo))
                            )
                        }
                        Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceAround) {
                            Box(
                                Modifier.width(36.dp).height(8.dp)
                                    .border(
                                        1.dp,
                                        profileView.color(photo),
                                        RoundedCornerShape(16.dp)
                                    )
                            )
                            Box(
                                Modifier.width(36.dp).height(8.dp)
                                    .border(
                                        1.dp,
                                        profileView.color(photo),
                                        RoundedCornerShape(16.dp)
                                    )
                            )
                        }
                    }
                }
                profileView.PhoneEntry(bio, "Bio", 1f, 0.dp) {
                    Column(Modifier.fillMaxSize().offset(y = (-16).dp), Arrangement.Center) {
                        Icon(Icons.Default.Person, null, Modifier.fillMaxWidth().aspectRatio(1f), tint = profileView.color(bio))
                    }
                    Column(
                        Modifier.fillMaxSize()
                            .offset(y = 80.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(8.dp)
                    ) {
                        Row(Modifier.padding(bottom = 4.dp)) {
                            Box(
                                Modifier.width(48.dp).height(4.dp)
                                    .background(profileView.color(bio))
                            )
                            Spacer(Modifier.width(4.dp))
                            Box(
                                Modifier.width(8.dp).height(4.dp)
                                    .background(profileView.color(bio))
                            )
                        }
                        Box(
                            Modifier.width(32.dp).height(2.dp)
                                .background(profileView.color(bio))
                        )
                        Row(Modifier.fillMaxWidth().padding(0.dp, 8.dp), Arrangement.SpaceAround) {
                            Box(
                                Modifier.width(36.dp).height(8.dp)
                                    .border(
                                        1.dp,
                                        profileView.color(bio),
                                        RoundedCornerShape(16.dp)
                                    )
                            )
                            Box(
                                Modifier.width(36.dp).height(8.dp)
                                    .border(
                                        1.dp,
                                        profileView.color(bio),
                                        RoundedCornerShape(16.dp)
                                    )
                            )
                        }
                        Box(
                            Modifier.width(32.dp).height(4.dp).align(Alignment.CenterHorizontally)
                                .background(profileView.color(bio))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(64.dp).height(2.dp)
                                .background(profileView.color(bio))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(72.dp).height(2.dp)
                                .background(profileView.color(bio))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(56.dp).height(2.dp)
                                .background(profileView.color(bio))
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(48.dp).height(2.dp)
                                .background(profileView.color(bio))
                        )
                    }
                }
            }
            ListCategory("Theme") {
                val default = "theme_orange"
                val switch = ImageSwitch(default)

                Row(Modifier.padding(8.dp, 0.dp).horizontalScroll(ScrollState(0))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        switch.ThemeEntry("theme_system", "System",
                            if (isSystemInDarkTheme())
                                dynamicDarkColorScheme(LocalContext.current).primary
                            else
                                dynamicLightColorScheme(LocalContext.current).primary
                        )
                    switch.ThemeEntry("theme_orange", "Orange", DarkOrange)
                    switch.ThemeEntry("theme_red", "Red", Color.Red)
                    switch.ThemeEntry("theme_yellow", "Yellow", Color.Yellow)
                    switch.ThemeEntry("theme_lime", "Lime", Color.Unspecified)
                    switch.ThemeEntry("theme_green", "Green", Color.Green)
                    switch.ThemeEntry("theme_mint", "Mint", Color.Cyan)
                    switch.ThemeEntry("theme_teal", "Teal", Color.Cyan)
                    switch.ThemeEntry("theme_blue", "Blue", Color.Blue)
                    switch.ThemeEntry("theme_violet", "Violet", Color(1f, 0f, 1f))
                    switch.ThemeEntry("theme_purple", "Purple", Color(1f, 0f, 1f))
                    switch.ThemeEntry("theme_pink", "Pink", Color(1f, 0f, 1f))
                }
            }
            ListCategory("Location") {
                SwitchEntry("geo_loc", "Use geolocation")
                SwitchEntry("geo_update", "Update location in profile")
            }
        }
    }
}
