package com.charleex.vidgenius.ui.features.generation.local

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.features.uploads.AppFilledButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSection(
    videos: List<Video>,
    onStartAll: () -> Unit,
    onStartOne: (Video) -> Unit,
    onDelete: (String) -> Unit,
) {
    SectionContainer(
        name = "Local videos: ${videos.size}",
        isMainHeader = true,
        enabled = videos.isNotEmpty(),
        openInitially = true,
        extra = {
            AnimatedVisibility(
                visible = videos.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedButton(
                    onClick = {
                        videos.forEach {
                            onDelete(it.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Delete All")
                }
            }
            AnimatedVisibility(
                visible = videos.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AppFilledButton(
                    label = "Start All",
                    imageVector = Icons.Default.PlayArrow,
                    isLoading = false,
                    onClick = onStartAll,
                )
            }
        },
    ) {
        Surface(
            tonalElevation = 2.dp
        ) {
            AnimatedVisibility(videos.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No local videos.",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(64.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                videos.forEachIndexed { index, video ->
                    val fileName = File(video.path).nameWithoutExtension

                    ListItem(
                        headlineText = { Text(text = fileName) },
                        trailingContent = {
                            AppFilledButton(
                                label = "Delete",
                                imageVector = Icons.Default.Delete,
                                isLoading = false,
                                onClick = {
                                    onDelete(video.id)
                                },
                            )
                            AppFilledButton(
                                label = "Start",
                                imageVector = Icons.Default.PlayArrow,
                                isLoading = false,
                                onClick = {
                                    onStartOne(video)
                                },
                            )
                        },
                    )
                    if (index != videos.size - 1) {
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
