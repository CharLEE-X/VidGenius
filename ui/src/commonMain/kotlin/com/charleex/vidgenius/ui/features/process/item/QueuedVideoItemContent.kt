package com.charleex.vidgenius.ui.features.process.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.CounterAnimation
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun QueuedVideoItemContent(
    modifier: Modifier = Modifier,
    uiVideo: UiVideo,
    breakpoint: Breakpoint,
    youtubeUploadOn: Boolean,
    onDeleteClicked: () -> Unit,
    onStartProcessingClicked: () -> Unit,
) {
    var progress by remember { mutableStateOf(0) }
    val animatedProgress by animateIntAsState(targetValue = progress)

    LaunchedEffect(uiVideo) {
        if (youtubeUploadOn && uiVideo.hasYoutubeVideoId()) {
            progress = 100
        } else if (uiVideo.hasMetadata()) {
            progress = if (youtubeUploadOn) 80 else 100
        } else if (uiVideo.hasContext()) {
            progress = 60
        } else if (uiVideo.hasDescriptions(3)) {
            progress = 50
        } else if (uiVideo.hasScreenshots(3)) {
            progress = 30
        }
    }
    SectionContainer(
        name = uiVideo.path,
        openInitially = false,
        progress = animatedProgress,
        extra = {
            AnimatedVisibility(
                visible = progress > 0,
                enter = fadeIn(),
                exit = fadeOut(),
            )
            {
                CounterAnimation(
                    count = animatedProgress,
                ) {
                    Text(
                        text = "$it%",
                        color = Color.White,
                    )
                }
            }
            AppOutlinedButton(
                label = "Delete",
                icon = Icons.Default.Delete,
                onClick = onDeleteClicked,
            )
            AppOutlinedButton(
                label = "Start",
                icon = Icons.Default.PlayArrow,
                onClick = onStartProcessingClicked,
            )
        },
        modifier = modifier
    ) {
        ContentText(
            uiVideo = uiVideo,
            modifier = Modifier.padding(32.dp),
        )
    }
}
