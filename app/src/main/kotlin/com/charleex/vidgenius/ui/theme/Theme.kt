package com.charleex.vidgenius.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

val AppLightColorScheme = lightColorScheme(
    // M3 light Color parameters
)
val AppDarkColorScheme = darkColorScheme(
    // M3 dark Color parameters
)

@Composable
fun AutoYtVidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val AppColorScheme = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes
    ) {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            content()
        }
    }
}
