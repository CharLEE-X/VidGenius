package com.charleex.vidgenius.ui.components.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import kotlinx.coroutines.delay

@Composable
internal fun NoVideos(
    visible: Boolean,
) {
    var text by remember { mutableStateOf("No videos") }

    LaunchedEffect(visible) {
        if (visible) {
            delay(400)
            text = "No videos."
            delay(400)
            text = "No videos.."
            delay(400)
            text = "No videos..."
            delay(400)
            text = "No videos... :"
            delay(400)
            text = "No videos... :("
        }
    }
    AnimatedVisibility(visible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "No videos... :(",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NoVideosPreview() {
    AppTheme {
        Surface {
            Column {
                NoVideos(visible = true)
            }
        }
    }
}
