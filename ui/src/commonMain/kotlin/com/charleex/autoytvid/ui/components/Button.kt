package com.charleex.autoytvid.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.charleex.autoytvid.ui.util.shadow

@Suppress("FunctionName")
@Composable
internal fun AppButton(
    modifier: Modifier = Modifier,
    label: @Composable (Modifier) -> Unit,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(percent = 20),
    buttonColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    isLoading: Boolean = false,
    hasShadow: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleState by animateFloatAsState(if (isPressed) .99f else 1f)
    val shadowOffsetY by animateDpAsState(if (isPressed) 8.dp else 16.dp)

    Button(
        onClick = onClick,
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonColor,
            contentColor = contentColor
        ),
        elevation = null,
        modifier = modifier
            .padding(bottom = if (hasShadow) 16.dp else 0.dp)
            .shadow(
                color2 = if (hasShadow) MaterialTheme.colors.primary.copy(alpha = 0.7f) else Color.Transparent,
                offset = shadowOffsetY,
                blurRadius = if (hasShadow) 30.dp else 0.dp
            )
            .graphicsLayer {
                scaleX = scaleState
                scaleY = scaleState
            },
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            if (isLoading)
                CircularProgressIndicator(
                    color = contentColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(16.dp)
                )
            else
                label(Modifier)
        }
    }
}
