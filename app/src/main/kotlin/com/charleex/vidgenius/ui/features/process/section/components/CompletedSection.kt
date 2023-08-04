package com.charleex.vidgenius.ui.features.process.section.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.SectionContainer

@Composable
fun CompletedSection(
    videos: List<Video>,
    onDelete: (String) -> Unit,
) {
    val completed = videos.filter { it.isCompleted }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    SectionContainer(
        name = "Completed videos: ${videos.size}",
        headerBgColor = Color.Green,
        isMainHeader = true,
        extra = {
            AnimatedVisibility(completed.isNotEmpty()) {
                if (showDeleteConfirm) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        AppOutlinedButton(
                            label = "YES",
                            icon = null,
                            bgColor = Color.Green,
                            labelColor = Color.Black,
                            iconTint = Color.Black,
                            onClick = {
                                showDeleteConfirm = false
                                completed.forEach {
                                    onDelete(it.id)
                                }
                            },
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        AppOutlinedButton(
                            label = "NO",
                            bgColor = Color.Red,
                            labelColor = Color.Black,
                            iconTint = Color.Black,
                            icon = null,
                            onClick = { showDeleteConfirm = false },
                        )
                    }
                } else {
                    AppOutlinedButton(
                        label = "Delete",
                        icon = Icons.Default.PlayArrow,
                        onClick = { showDeleteConfirm = true },
                    )
                }
            }
        },
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
                        video = video,
                        onDeleteClicked = {
                            onDelete(video.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
        }
    }
}
