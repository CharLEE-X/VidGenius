package com.charleex.vidgenius.ui.features.process.section.completed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.SectionContainer

@Composable
fun CompletedSection(
    videos: List<Video>,
    onDelete: (String) -> Unit,
) {
    SectionContainer(
        name = "Completed videos: ${videos.size}",
        headerBgColor = Color.Green,
        isMainHeader = true,
        extra = {},
    ) {
        AnimatedVisibility(videos.none { it.isCompleted }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No videos completed.",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(64.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            videos
                .filter { it.isCompleted }
                .forEach { video ->
                    CompletedVideo(
                        uiVideo = video,
                        onDeleteClicked = {
                            onDelete(video.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
        }
    }
}
