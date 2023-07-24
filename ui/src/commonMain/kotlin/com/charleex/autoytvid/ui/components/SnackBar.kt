package com.charleex.autoytvid.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
internal fun AppSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = RoundedCornerShape(percent = 35),
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    actionColor: Color = MaterialTheme.colors.secondary,
    elevation: Dp = 20.dp,
) {
    Snackbar(
        snackbarData = snackBarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        actionColor = actionColor,
        elevation = elevation,
    )
}

@Suppress("FunctionName")
@Composable
fun KXSnackBarHost(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp)
            .padding(horizontal = 16.dp)
            .padding(bottom = 64.dp + 64.dp),
        snackbar = { snackbarData -> AppSnackBar(snackbarData) }
    )
}

internal class SnackData(
    override val message: String = "Snackbar preview",
    override val actionLabel: String? = "Action",
    override val duration: SnackbarDuration = SnackbarDuration.Indefinite,
) : SnackbarData {
    override fun dismiss() {}
    override fun performAction() {}
}
