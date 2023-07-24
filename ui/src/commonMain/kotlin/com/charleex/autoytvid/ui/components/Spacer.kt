package com.charleex.autoytvid.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
internal fun AppSpacer(
    size: Int = 16,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier.size(size.dp))
}

@Composable
internal fun RowScope.AppFlexSpacer(
    weight: Float = 1f,
) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
internal fun ColumnScope.AppFlexSpacer(
    weight: Float = 1f,
) {
    Spacer(modifier = Modifier.weight(weight))
}
