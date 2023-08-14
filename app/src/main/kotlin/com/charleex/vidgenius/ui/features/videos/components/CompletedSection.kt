package com.charleex.vidgenius.ui.features.videos.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.model.LocalVideo
import com.charleex.vidgenius.ui.components.SectionContainer

@Composable
fun CompletedSection(
    videos: List<LocalVideo>,
    onDelete: (String) -> Unit,
) {
    val completed = videos.filter { it.isCompleted }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    SectionContainer(
        name = "Completed videos: ${videos.size}",
        isMainHeader = true,
        extra = {
            AnimatedVisibility(completed.isNotEmpty()) {
                if (showDeleteConfirm) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        ElevatedButton(
                            onClick = {
                                showDeleteConfirm = false
                                completed.forEach {
                                    onDelete(it.id)
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                            )
                            Text(
                                text = "Delete",
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        FilledTonalIconButton(
                            onClick = {
                                showDeleteConfirm = false
                            },
                        ) {
                            Text(
                                text = "Cancel",
                            )
                        }
                    }
                } else {
                    ElevatedButton(
                        onClick = {
                            showDeleteConfirm = true
                            completed.forEach {
                                onDelete(it.id)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                        )
                        Text(
                            text = "Delete",
                        )
                    }
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
                    style = MaterialTheme.typography.headlineLarge,
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
