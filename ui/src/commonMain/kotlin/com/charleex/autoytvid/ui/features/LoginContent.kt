package com.charleex.autoytvid.ui.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charleex.autoytvid.ui.util.Breakpoint

@Composable
internal fun LoginContent(
    modifier: Modifier,
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Login")
    }
}
