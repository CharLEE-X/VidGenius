package com.charleex.vidgenius.ui.features.generation.local

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.features.generation.components.ContentText
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
//                AppOutlinedButton(
//                    label = "Not on YT",
//                    icon = Icons.Default.Close,
//                    iconTint = Color.Red,
//                    enabled = true,
//                    onClick = {}
//                )
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
//                    AppOutlinedButton(
//                        label = "YES",
//                        icon = null,
//                        bgColor = Color.Green,
//                        labelColor = Color.Black,
//                        iconTint = Color.Black,
//                        onClick = {
//                            onDeleteClicked()
//                            showDeleteConfirm = false
//                        },
//                    )
//                    Spacer(modifier = Modifier.size(8.dp))
//                    AppOutlinedButton(
//                        label = "NO",
//                        bgColor = Color.Red,
//                        labelColor = Color.Black,
//                        iconTint = Color.Black,
//                        icon = null,
//                        onClick = { showDeleteConfirm = false },
//                    )
                }
            } else {
//                AppOutlinedButton(
//                    label = "Delete",
//                    icon = Icons.Default.PlayArrow,
//                    onClick = { showDeleteConfirm = true },
//                )
            }
//            AppOutlinedButton(
//                label = "Start",
//                icon = Icons.Default.CopyAll,
//                enabled = isOnYt,
//                onClick = {
//                    onStartClicked(video.id)
//                },
//            )
        }
    ) {
        ContentText(
            video = video,
            modifier = Modifier.padding(32.dp),
        )
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
            contentInfo.hi.title.isNotEmpty() &&
            contentInfo.hi.description.isNotEmpty() &&
            contentInfo.tags.isNotEmpty() &&
            contentInfo.tags.all { it.isNotEmpty() }
}
