package com.charleex.vidgenius.ui.features.subtitles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AnimalsSubtitlesContent(
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Animals")
            SubtitlesContent()
        }
    }
}

@Composable
fun FailsSubtitlesContent(
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Fails")
            SubtitlesContent()
        }
    }
}

@Composable
fun SubtitlesContent(
) {
    Text(text = "Subtitles")
}
