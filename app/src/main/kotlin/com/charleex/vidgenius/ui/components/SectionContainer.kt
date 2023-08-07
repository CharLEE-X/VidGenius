package com.charleex.vidgenius.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SectionContainer(
    modifier: Modifier = Modifier,
    name: String,
    enabled: Boolean = true,
    openInitially: Boolean = true,
    progress: Int? = null,
    isMainHeader: Boolean = false,
    extra: @Composable RowScope.() -> Unit,
    block: @Composable ColumnScope.() -> Unit,
) {
    val indicationSource = remember { MutableInteractionSource() }
    var isOpen by remember { mutableStateOf(openInitially) }
    var isHeaderHovered by remember { mutableStateOf(false) }
    val iconRotationState by animateFloatAsState(if (isOpen) 180f else 0f)

    Card(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            Surface(
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .onPointerEvent(PointerEventType.Enter) {
                        isHeaderHovered = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        isHeaderHovered = false
                    }
                    .pointerHoverIcon(
                        if (isHeaderHovered && enabled)
                            PointerIcon.Hand else PointerIcon.Default
                    )
                    .clickable(
                        indication = null,
                        interactionSource = indicationSource,
                        onClick = { isOpen = !isOpen },
                        enabled = enabled,
                        role = Role.Button,
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(
                            start = 48.dp,
                            end = if (isMainHeader) 38.dp else 16.dp,
                        )
                ) {
                    Text(
                        text = name,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    extra()
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .alpha(if (enabled) 1f else 0f)
                            .graphicsLayer(
                                rotationZ = iconRotationState,
                            )
                    )
                }
            }
            progress?.let {
                LinearProgressIndicator(
                    progress = it / 100f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AnimatedVisibility(isOpen) {
                block()
            }
        }
    }
}
