package com.hackathon.cda.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val green = Color(0xff03c955)
val userMessageBackground = green
val userMessageText = Color.Black
val assistantMessageBackground = Color(0xffd8d8d8)
val assistantMessageText = Color(0xff000000)


val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val Neutral8 = Color(0xff121212)
val Neutral7 = Color(0xde000000)
val Neutral6 = Color(0x99000000)
val Neutral5 = Color(0x61000000)
val Neutral4 = Color(0x1f000000)
val Neutral3 = Color(0x2D888888)
val Neutral2 = Color(0xffededed)
val Neutral1 = Color(0xbdffffff)
val Neutral0 = Color(0xffffffff)
val NeutralTransparent = Color(0x0)
val Orange1 = Color(0xFFEF5350)

val primary = Color(0xFFEC407A)
val secondary = Purple500

object AppColors {
    val Purple200 = Color(0xFFBB86FC)
    val Purple500 = Color(0xFF6200EE)

    object Light {
        val BackgroundTop = Color(0XFFEEF0F5)
        val BackgroundBottom = Color(0xFFFFFFFF)
        val LightShadow = Color(0xFFFFFFFF)
        val DarkShadow = Color(0xFFA8B5C7)
        val TextColor = Neutral7
    }

    object Dark {
        val BackgroundTop = Color(0XFF394253)
        val BackgroundBottom = Color(0XFF181C27)
        val LightShadow = Color(0x66494949)
        val DarkShadow = Color(0x66000000)
        val TextColor = Neutral1
    }

    @Composable
    fun lightShadow() = if (!isSystemInDarkTheme()) Light.LightShadow else Dark.LightShadow

    @Composable
    fun darkShadow() = if (!isSystemInDarkTheme()) Light.DarkShadow else Dark.DarkShadow
}

val pickerColors = listOf(
    null,
    Color(0xFF000000),
    Color(0xFFFFFFFF),
    Color(0xFFFAFAFA),
    Color(0x80FF4444),
    Color(0xFFEF5350),
    Color(0xFFEC407A),
    Color(0xFFAB47BC),
    Color(0xFF7E57C2),
    Color(0xFF5C6BC0),
    Color(0xFF42A5F5),
    Color(0xFF29B6F6),
    Color(0xFF26C6DA),
    Color(0xFF26A69A),
    Color(0xFF66BB6A),
    Color(0xFF9CCC65),
    Color(0xFFD4E157),
    Color(0xFFFFEE58),
    Color(0xFFFFCA28),
    Color(0xFFFFA726),
    Color(0xFFFF7043)
)
