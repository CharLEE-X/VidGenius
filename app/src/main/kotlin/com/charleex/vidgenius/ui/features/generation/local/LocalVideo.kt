package com.charleex.vidgenius.ui.features.generation.local

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.util.pretty
import java.util.Locale

@Composable
internal fun LocalVideo(
    modifier: Modifier = Modifier,
    video: Video,
    isOnYt: Boolean = true,
    onDeleteClicked: () -> Unit,
    onStartClicked: (String) -> Unit,
) {
    val descWithTag = "${
        video.tags.joinToString(", ") {
            "#${
                it.replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.UK) else it.toString()
                }
            }"
        }
    }\n\n" +
            "${video.description ?: "No description"}\n\n" +
            "Youtube: @RoaringAnimals-FunnyAnimals\n" +
            "TikTok: @Roaring_Laughter"

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
        name = video.youtubeName,
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
            descWithTag = descWithTag,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
internal fun ContentText(
    video: Video,
    descWithTag: String,
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                video.title?.let {
                    SelectionContainer {
                        Text(
                            text = it,
                        )
                    }

//                    AppOutlinedButton(
//                        label = "COPY",
//                        icon = Icons.Default.CopyAll,
//                        onClick = {
//                            clipboardManager.setText(AnnotatedString(it))
//                        },
//                        modifier = Modifier
//                            .padding(end = 48.dp)
//                            .align(Alignment.CenterEnd)
//                    )
                } ?: Text(
                    text = "No title",
                )
            }
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    SelectionContainer {
                        Text(
                            text = descWithTag,
                            modifier = Modifier.fillMaxWidth(.8f)
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(descWithTag))
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "COPY",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            Text(
                text = "Youtube Video Id: ${video.youtubeName}",
                color = MaterialTheme.colorScheme.onSurface,
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
    return !title.isNullOrEmpty() &&
            !description.isNullOrEmpty() &&
            tags.isNotEmpty() &&
            tags.all { it.isNotEmpty() }
}
