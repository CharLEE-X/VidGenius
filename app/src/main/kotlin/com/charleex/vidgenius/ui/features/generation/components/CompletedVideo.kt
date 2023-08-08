package com.charleex.vidgenius.ui.features.generation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.util.pretty

@Composable
internal fun CompletedVideo(
    modifier: Modifier = Modifier,
    video: Video,
    isOnYt: Boolean = true,
    onDeleteClicked: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    SectionContainer(
        name = video.youtubeId,
        openInitially = false,
        modifier = modifier,
        extra = {
            AnimatedVisibility(!isOnYt) {
                OutlinedButton(
                    enabled = false,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                    Text(
                        text = "Not on YT",
                    )
                }
            }
            SelectionContainer {
                Text(
                    text = video.modifiedAt.pretty(),
                    modifier = Modifier
                )
            }
            if (showDeleteConfirm) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    OutlinedButton(
                        enabled = false,
                        onClick = {
                            onDeleteClicked()
                            showDeleteConfirm = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                        )
                        Text(
                            text = "Delete",
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedButton(
                        enabled = false,
                        onClick = {
                            showDeleteConfirm = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                        Text(
                            text = "Cancel",
                        )
                    }
                }
            } else {
                ElevatedButton(
                    enabled = false,
                    onClick = {
                        showDeleteConfirm = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                    Text(
                        text = "Delete",
                    )
                }
            }
        }
    ) {
        ContentText(
            video = video,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
fun ContentText(
    video: Video,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

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
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(start = 16.dp),
                        ) {
                            video.descriptions.forEach {
                                Text(
                                    text = "- $it",
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
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LocalizationItem(
                        languageCode = "English",
                        title = video.contentInfo.enUS.title,
                        description = video.contentInfo.enUS.description,
                    )
                    LocalizationItem(
                        languageCode = "Spanish",
                        title = video.contentInfo.es.title,
                        description = video.contentInfo.es.description,
                    )
                    LocalizationItem(
                        languageCode = "Hindi",
                        title = video.contentInfo.hi.title,
                        description = video.contentInfo.hi.description,
                    )
                    Text(
                        text = "Tags: ${video.contentInfo.tags.joinToString(", ")}",
                    )
                }
            }
            Text(
                text = "Youtube Video Id: ${video.youtubeId}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun LocalizationItem(
    languageCode: String,
    title: String,
    description: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = languageCode,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(
                text = title,
            )
            Text(
                text = description,
            )
        }
    }
}
