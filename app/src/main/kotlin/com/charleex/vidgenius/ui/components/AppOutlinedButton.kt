package com.charleex.vidgenius.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import jdk.jfr.Enabled
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AppOutlinedButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    bgColor: Color = MaterialTheme.colors.surface,
    labelColor: Color = MaterialTheme.colors.onSurface,
    iconTint: Color = MaterialTheme.colors.onSurface,
    onClick: () -> Unit,
) {
    val indicationSource = remember { MutableInteractionSource() }
    var isHeaderHovered by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        if (isHeaderHovered && enabled) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
    )
    val enabledTextColor by animateColorAsState(
        if (enabled) MaterialTheme.colors.primary else Color.Gray
    )
    val textColor by animateColorAsState(
        if (isCopied) MaterialTheme.colors.primary else enabledTextColor
    )

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    OutlinedButton(
        shape = RoundedCornerShape(10.dp),
        elevation = null,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor,
        ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bgColor,
        ),
        onClick = {
            isCopied = true
            onClick()
        },
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) {
                isHeaderHovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                isHeaderHovered = false
            }
            .pointerHoverIcon(
                if (isHeaderHovered)
                    PointerIcon.Hand else PointerIcon.Default
            )
    ) {
        icon?.let {
            AnimatedVisibility(isCopied) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )
            }
            AnimatedVisibility(!isCopied) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.button,
            color = textColor,
        )
    }
}
