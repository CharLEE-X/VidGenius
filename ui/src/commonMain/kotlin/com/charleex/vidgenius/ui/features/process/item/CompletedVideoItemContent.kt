package com.charleex.vidgenius.ui.features.process.item

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
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.util.Breakpoint
import com.charleex.vidgenius.ui.util.pretty
import java.io.File
import java.util.Locale

@Composable
internal fun CompletedVideoItemContent(
    modifier: Modifier = Modifier,
    uiVideo: UiVideo,
    breakpoint: Breakpoint,
    onDeleteClicked: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val name = File(uiVideo.path).name
    val descWithTag = "${
        uiVideo.tags.joinToString(", ") {
            "#${
                it.replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.UK) else it.toString()
                }
            }"
        }
    }\n\n" +
            "${uiVideo.description ?: "No description"}\n\n" +
            "Youtube: @RoaringAnimals-FunnyAnimals\n" +
            "TikTok: @Roaring_Laughter"

    var showDeleteConfirm by remember { mutableStateOf(false) }

    SectionContainer(
        name = name,
        openInitially = false,
        modifier = modifier,
        extra = {
            SelectionContainer {
                Text(
                    text = uiVideo.modifiedAt.pretty(),
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
                label = "COPY",
                icon = Icons.Default.CopyAll,
                onClick = {
                    clipboardManager.setText(AnnotatedString(descWithTag))
                },
            )
        }
    ) {
        ContentText(
            uiVideo = uiVideo,
            descWithTag = descWithTag,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
internal fun ContentText(
    uiVideo: UiVideo,
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
            uiVideo.screenshots.forEach {
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
                            uiVideo.descriptions.forEach {
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
                            text = "Context: ${uiVideo.descriptionContext}",
                            color = MaterialTheme.colors.onSurface,
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
                uiVideo.title?.let {
                    SelectionContainer {
                        Text(
                            text = it,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }

                    AppOutlinedButton(
                        label = "COPY",
                        icon = Icons.Default.CopyAll,
                        onClick = {
                            clipboardManager.setText(AnnotatedString(it))
                        },
                        modifier = Modifier
                            .padding(end = 48.dp)
                            .align(Alignment.CenterEnd)
                    )
                } ?: Text(
                    text = "No title",
                    color = MaterialTheme.colors.onSurface,
                )
            }
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary
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
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.fillMaxWidth(.8f)
                        )
                    }
                    AppOutlinedButton(
                        label = "COPY",
                        icon = Icons.Default.CopyAll,
                        onClick = {
                            clipboardManager.setText(AnnotatedString(descWithTag))
                        },
                    )
                }
            }
            Text(
                text = "Youtube Video Id: ${uiVideo.youtubeVideoId}",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
