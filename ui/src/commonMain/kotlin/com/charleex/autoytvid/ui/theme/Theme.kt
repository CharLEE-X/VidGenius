package com.charleex.autoytvid.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hackathon.cda.ui.theme.Neutral2
import com.hackathon.cda.ui.theme.Neutral3
import com.hackathon.cda.ui.theme.Neutral6
import com.hackathon.cda.ui.theme.Neutral7
import com.hackathon.cda.ui.theme.Purple40
import com.hackathon.cda.ui.theme.Typography
import com.hackathon.cda.ui.theme.primary
import com.hackathon.cda.ui.theme.secondary

private val DarkColorPalette = darkColors(
    primary = primary,
    onPrimary = Color.White,
    secondary = secondary,
    onSecondary = Color.White,
    surface = Neutral6,
    onSurface = Color.White,
    background = Neutral7,
    onBackground = Color.White,
)

private val LightColorPalette = lightColors(
    primary = primary,
    onPrimary = Color.White,
    secondary = secondary,
    onSecondary = Color.White,
    background = Neutral2,
    onBackground = Color.DarkGray,
    surface = Color.White,
    onSurface = Color.Black,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AutoYtVidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography(),
        shapes = shapes,
    ) {
        Surface(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            content()
        }
    }
}
