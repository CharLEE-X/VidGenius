package com.charleex.vidgenius.ui.features.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.YoutubeSearchedFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video_item.ProcessVideoItemContract
import com.charleex.vidgenius.feature.process_video_item.ProcessVideoItemViewModel
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.util.Breakpoint
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ProcessVideoItemContent(
    modifier: Modifier = Modifier,
    uiVideo: UiVideo,
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideoItemViewModel(
            scope = scope,
            uiVideo = uiVideo,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(isVisible) {
        SectionContainer(
            name = uiVideo.path,
            isOpen = false,
            extra = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FloatingActionButton(
                        onClick = { },
                        backgroundColor = if (uiVideo.hasScreenshots(3)) Color.Green else Color.LightGray,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                        )
                    }
                    FloatingActionButton(
                        onClick = { },
                        backgroundColor = if (uiVideo.hasDescriptions(3)) Color.Green else Color.LightGray,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                        )
                    }
                    FloatingActionButton(
                        onClick = { },
                        backgroundColor = if (uiVideo.hasContext()) Color.Green else Color.LightGray,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                        )
                    }
                    FloatingActionButton(
                        onClick = { },
                        backgroundColor = if (uiVideo.hasMetadata()) Color.Green else Color.LightGray,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataArray,
                            contentDescription = null,
                        )
                    }
                    FloatingActionButton(
                        onClick = { },
                        backgroundColor = if (uiVideo.hasYoutubeVideoId()) Color.Green else Color.LightGray,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.YoutubeSearchedFor,
                            contentDescription = null,
                        )
                    }
                    AppFlexSpacer()
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        vm.trySend(ProcessVideoItemContract.Inputs.Video.CancelProcessingVideo)
                                        onDeleteClicked()
                                    }
                                )
                        )
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    )
                    {
                        FloatingActionButton(
                            onClick = { vm.trySend(ProcessVideoItemContract.Inputs.Video.StartVideoProcessing) },
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Video Processing",
                            )
                        }
                    }
                }
            },
            modifier = Modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp),
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
                        ) {
                            // TODO: Placeholder
                        }
                    }
                }
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
                Text(
                    text = "Context: ${uiVideo.descriptionContext}",
                    color = MaterialTheme.colors.onSurface,
                )
                Text(
                    text = "Title: ${uiVideo.title}",
                    color = MaterialTheme.colors.onSurface,
                )
                Text(
                    text = "Description: ${uiVideo.description}",
                    color = MaterialTheme.colors.onSurface,
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Tags: ",
                        color = MaterialTheme.colors.onSurface,
                    )
                    uiVideo.tags.forEach {
                        Text(
                            text = it,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }
                }
                Text(
                    text = "Youtube Video Id: ${uiVideo.youtubeVideoId}",
                    color = MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}
