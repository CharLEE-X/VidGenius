package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.shadow

@Composable
internal fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    shadowColor: Color = MaterialTheme.colors.primary,
    hasShadow: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        shape = shape,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = modifier
            .shadow(
                color2 = if (hasShadow) shadowColor.copy(alpha = 0.7f) else Color.Transparent,
                offset = if (hasShadow) 8.dp else 16.dp,
                blurRadius = if (hasShadow) 20.dp else 0.dp
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            content()
        }
    }
}
