package com.charleex.vidgenius.ui

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.Breakpoint
import kotlinx.coroutines.flow.MutableStateFlow

internal object AppState {
    val windowSize = MutableStateFlow(
        DpSize(
            width = Breakpoint.DESKTOP_SMALL.value.dp,
            height = Breakpoint.DESKTOP_SMALL.value.dp,
        )
    )
}

