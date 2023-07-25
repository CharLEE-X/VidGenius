package com.charleex.vidgenius.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.animateTextStyleAsState

@Composable
internal fun AppRadioGroup(
    items: List<String>,
    selected: String,
    setSelected: (selected: String) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            items.forEach { item ->
                RadioItem(
                    item = item,
                    label = item,
                    isSelected = selected == item,
                    setSelected = setSelected,
                )
            }
        }
    }
}

@Composable
internal fun RadioItem(
    item: String,
    label: String,
    isSelected: Boolean,
    setSelected: (selected: String) -> Unit,
    selectedColor: Color = MaterialTheme.colors.secondary,
) {
    val textStyleState by animateTextStyleAsState(
        targetValue = if (isSelected)
            MaterialTheme.typography.body1 else MaterialTheme.typography.body2,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                onClick = { setSelected(item) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { setSelected(item) },
            enabled = true,
            colors = RadioButtonDefaults.colors(
                selectedColor = selectedColor
            )
        )
        Text(
            text = label,
            style = textStyleState,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
