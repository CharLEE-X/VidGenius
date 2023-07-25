package com.charleex.vidgenius.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.Breakpoint
import com.charleex.vidgenius.ui.util.toBreakpoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

internal object AppState {
    var navBarOpenCloseState by mutableStateOf(false)
    val windowSize = MutableStateFlow(
        DpSize(
            width = Breakpoint.DESKTOP_SMALL.value.dp,
            height = Breakpoint.DESKTOP_SMALL.value.dp,
        )
    )
    val currentBreakpoint = windowSize.asStateFlow().map { it.toBreakpoint() }

    var safePaddingValues by mutableStateOf(PaddingValues())
}

