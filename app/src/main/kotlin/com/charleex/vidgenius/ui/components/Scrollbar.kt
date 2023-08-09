package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun BoxScope.AppVerticalScrollbar(
    modifier: Modifier = Modifier,
    adapter: ScrollbarAdapter,
    shape: CornerBasedShape = CutCornerShape(8.dp),
    minimalHeight: Dp = 48.dp,
    thickness: Dp = 10.dp,
    hoverDuration: Int = 300,
    unHoverColor: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
    hoverColor: Color = MaterialTheme.colorScheme.tertiary.copy(alpha = 1f),
) {
    VerticalScrollbar(
        style = ScrollbarStyle(
            shape = shape,
            minimalHeight = minimalHeight,
            thickness = thickness,
            hoverDurationMillis = hoverDuration,
            unhoverColor = unHoverColor,
            hoverColor = hoverColor,
        ),
        modifier = modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight(),
        adapter = adapter,
    )
}
