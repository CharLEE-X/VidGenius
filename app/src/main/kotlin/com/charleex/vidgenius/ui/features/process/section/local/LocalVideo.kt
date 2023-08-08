package com.charleex.vidgenius.ui.features.process.section.local

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.util.pretty

@Composable
internal fun LocalVideo(
    modifier: Modifier = Modifier,
    video: Video,
    isOnYt: Boolean = true,
    onDeleteClicked: () -> Unit,
    onStartClicked: (String) -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var progress by remember { mutableStateOf(0) }
    val animatedProgress by animateIntAsState(targetValue = progress)

    LaunchedEffect(video) {
        if (video.isCompleted) {
            progress = 100
        } else if (video.hasMetadata()) {
            progress = 80
        } else if (video.hasContext()) {
            progress = 60
        } else if (video.hasDescriptions(3)) {
            progress = 50
        } else if (video.hasScreenshots(3)) {
            progress = 30
        }
    }

    SectionContainer(
        name = video.youtubeId,
        openInitially = false,
        modifier = modifier,
        progress = animatedProgress,
        extra = {
            AnimatedVisibility(!isOnYt) {
                AppOutlinedButton(
                    label = "Not on YT",
                    icon = Icons.Default.Close,
                    iconTint = Color.Red,
                    enabled = true,
                    onClick = {}
                )
            }
            SelectionContainer {
                Text(
                    text = video.modifiedAt.pretty(),
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                )
            }
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
                            onDeleteClicked()
                            showDeleteConfirm = false
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
            AppOutlinedButton(
                label = "Start",
                icon = Icons.Default.CopyAll,
                enabled = isOnYt,
                onClick = {
                    onStartClicked(video.id)
                },
            )
        }
    ) {
        ContentText(
            video = video,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
internal fun ContentText(
    video: Video,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            video.screenshots.forEach {
                LocalImage(
                    filePath = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                ) {}
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SelectionContainer {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Descriptions",
                            color = MaterialTheme.colors.onSurface,
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(start = 16.dp),
                        ) {
                            video.descriptions.forEach {
                                Text(
                                    text = "- $it",
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    SelectionContainer {
                        Text(
                            text = "Context: ${video.descriptionContext}",
                            color = MaterialTheme.colors.onSurface,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TAGS: ${video.contentInfo.tags.joinToString(", ")}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "enUS:",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            Text(
                text = "title: ${video.contentInfo.enUS.title}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "description: ${video.contentInfo.enUS.description}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "es:",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            Text(
                text = "title: ${video.contentInfo.es.title}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "description: ${video.contentInfo.es.description}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "pt:",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            Text(
                text = "title: ${video.contentInfo.pt.title}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "description: ${video.contentInfo.pt.description}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "zh:",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            Text(
                text = "title: ${video.contentInfo.fr.title}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "description: ${video.contentInfo.fr.description}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "hi:",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            Text(
                text = "title: ${video.contentInfo.hi.title}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Text(
                text = "description: ${video.contentInfo.hi.description}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Youtube Video Id: ${video.youtubeId}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun Video.hasScreenshots(numberOfScreenshots: Int): Boolean {
    return screenshots.isNotEmpty() &&
            screenshots.all { it.isNotEmpty() } &&
            screenshots.size == numberOfScreenshots
}

fun Video.hasDescriptions(numberOfDescriptions: Int): Boolean {
    return descriptions.isNotEmpty() &&
            descriptions.all { it.isNotEmpty() } &&
            descriptions.size == numberOfDescriptions
}

fun Video.hasContext(): Boolean {
    return !descriptionContext.isNullOrEmpty()
}

fun Video.hasMetadata(): Boolean {
    return contentInfo.enUS.title.isNotEmpty() &&
            contentInfo.enUS.description.isNotEmpty() &&
            contentInfo.es.title.isNotEmpty() &&
            contentInfo.es.description.isNotEmpty() &&
            contentInfo.pt.title.isNotEmpty() &&
            contentInfo.pt.description.isNotEmpty() &&
            contentInfo.fr.title.isNotEmpty() &&
            contentInfo.fr.description.isNotEmpty() &&
            contentInfo.hi.title.isNotEmpty() &&
            contentInfo.hi.description.isNotEmpty() &&
            contentInfo.tags.isNotEmpty()
}
