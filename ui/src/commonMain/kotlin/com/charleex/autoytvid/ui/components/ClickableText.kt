package com.charleex.autoytvid.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal fun ClickableText(
    text: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colors.onBackground,
    colorClicked: Color = MaterialTheme.colors.onSurface,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressed = updateTransition(isPressed)
    val colorState by pressed.animateColor { if (it) colorClicked else color }
    val scaleState by pressed.animateFloat { if (it) .9f else 1f }

    Text(
        text = text,
        color = colorState,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scaleState
                scaleY = scaleState
            }
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
    )
}
