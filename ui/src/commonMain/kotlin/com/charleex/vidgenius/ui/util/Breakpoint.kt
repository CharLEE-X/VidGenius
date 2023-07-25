package com.charleex.vidgenius.ui.util

import androidx.compose.ui.unit.DpSize

internal enum class Breakpoint(val value: Int) {
    MOBILE(480),
    TABLET(768),
    DESKTOP_SMALL(1024),
    DESKTOP(1200),
    DESKTOP_LARGE(1920),
}

internal fun DpSize.toBreakpoint(): Breakpoint = when {
    width.value < Breakpoint.TABLET.value -> Breakpoint.MOBILE
    width.value < Breakpoint.DESKTOP_SMALL.value -> Breakpoint.TABLET
    width.value < Breakpoint.DESKTOP.value -> Breakpoint.DESKTOP_SMALL
    width.value < Breakpoint.DESKTOP_LARGE.value -> Breakpoint.DESKTOP
    else -> Breakpoint.DESKTOP_LARGE
}
