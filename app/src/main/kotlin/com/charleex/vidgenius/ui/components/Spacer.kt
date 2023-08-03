package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun RowScope.AppFlexSpacer(
    weight: Float = 1f,
) {
    Spacer(modifier = Modifier.weight(weight))
}
