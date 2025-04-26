package dev.lisek.meetly.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRowScope
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.lisek.meetly.backend.util.Category
import dev.lisek.meetly.ui.theme.DarkOrange
import dev.lisek.meetly.ui.theme.LightOrange

@Composable
fun MultiChoiceSegmentedButtonRowScope.CategoryButton(category: Category, onClick: (Boolean) -> Unit) {
    var status by remember { mutableStateOf(false) }
    SegmentedButton(
        status,
        {
            status = !status
            onClick(status)
        },
        RoundedCornerShape(100),
        Modifier.padding(4.dp, 0.dp),
        true,
        SegmentedButtonDefaults.colors(LightOrange, Color.Black, DarkOrange),
        icon = { Icon(category.icon, null) }
    ) {
        Text(category.text)
    }
}
