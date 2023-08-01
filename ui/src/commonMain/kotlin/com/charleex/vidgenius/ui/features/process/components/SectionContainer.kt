package com.charleex.vidgenius.ui.features.process.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer


@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SectionContainer(
    modifier: Modifier = Modifier,
    name: String,
    isOpen: Boolean = false,
    headerBgColor: Color = MaterialTheme.colors.background.copy(alpha = 0.3f),
    extra: @Composable RowScope.() -> Unit,
    block: @Composable ColumnScope.() -> Unit,
) {
    var open by remember { mutableStateOf(isOpen) }
    val indicationSource = remember { MutableInteractionSource() }
    val headerBackgroundColor by animateColorAsState(
        targetValue = when (open) {
            true -> headerBgColor.copy(alpha = 0.1f)
            false -> headerBgColor.copy(alpha = 0.2f)
        }
    )

    AppCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            var isHeaderHovered by remember { mutableStateOf(false) }
            val iconRotationState by animateFloatAsState(if (isOpen) 180f else 0f)

            Surface(
                color = headerBackgroundColor,
                elevation = 0.dp,
                modifier = modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 20.dp,
                            horizontal = 48.dp
                        )
                        .onPointerEvent(PointerEventType.Enter) {
                            isHeaderHovered = true
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            isHeaderHovered = false
                        }
                        .pointerHoverIcon(if (isHeaderHovered) PointerIcon.Hand else PointerIcon.Default)
                        .clickable(
                            indication = null,
                            interactionSource = indicationSource,
                            onClick = {
                                open = !open
                            },
                            role = Role.Button,
                        )
                ) {
                    Text(
                        text = name,
                        color = MaterialTheme.colors.onSurface,
                    )
                    AppFlexSpacer()
                    extra()
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer(
                                rotationZ = iconRotationState,
                            )
                    )
                }
            }
            AnimatedVisibility(open) {
                block()
            }
        }
    }
}
