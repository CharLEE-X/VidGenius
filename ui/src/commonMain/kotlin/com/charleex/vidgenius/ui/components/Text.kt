package com.charleex.vidgenius.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
internal fun TitleText(
    text: String,
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface,
        // Need styling
    )
}

@Composable
internal fun ImportantText(
    text: String,
    color: Color = MaterialTheme.colors.onSurface,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 30.sp,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h4,
        color = color,
        fontWeight = fontWeight,
        fontSize = fontSize,
    )
}

@Composable
internal fun InfoText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colors.onSurface,
    fontWeight: FontWeight = FontWeight.Light,
    fontSize: TextUnit = 12.sp,
    softWrap: Boolean = false,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2,
        color = color,
        fontWeight = fontWeight,
        fontSize = fontSize,
        softWrap = softWrap,
        textAlign = textAlign,
        modifier = modifier,
    )
}
