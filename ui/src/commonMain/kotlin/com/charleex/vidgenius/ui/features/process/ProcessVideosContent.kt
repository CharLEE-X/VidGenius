package com.charleex.vidgenius.ui.features.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_videos.ProcessVideosContract
import com.charleex.vidgenius.feature.process_videos.ProcessVideosViewModel
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.DragArea
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun ProcessVideosContent(
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideosViewModel(
            scope = scope,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()
    var queued by remember { mutableStateOf(listOf<UiVideo>()) }
    var progress by remember { mutableStateOf(listOf<UiVideo>()) }
    var completed by remember { mutableStateOf(listOf<UiVideo>()) }
    val layColumnState = rememberLazyListState()

    LaunchedEffect(state.videos) {
        queued = state.videos.filter {
            !it.hasScreenshots(3) &&
                    !it.hasDescriptions(3) &&
                    !it.hasContext() &&
                    !it.hasMetadata() &&
                    !it.hasYoutubeVideoId()
        }
        progress = state.videos.filter {
            (
                    it.hasScreenshots(3) ||
                            it.hasDescriptions(3) ||
                            it.hasContext() ||
                            it.hasMetadata()
                    ) && !it.hasYoutubeVideoId()
        }
        completed = state.videos.filter { it.hasYoutubeVideoId() }

        println("videos: ${state.videos}")
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp)
        ) {
            item {
                SectionContainer(
                    name = "Drag and drop videos",
                    isOpen = queued.isEmpty(),
                    extra = {},
                    modifier = Modifier
                ) {
                    DragArea(
                        window = window,
                        onDropped = { files ->
                            vm.trySend(ProcessVideosContract.Inputs.HandleFiles(files))
                        },
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }
            item {
                SectionContainer(
                    name = "Queued videos: ${queued.size}",
                    headerBgColor = Color.LightGray,
                    isOpen = queued.isNotEmpty(),
                    extra = {
                        OutlinedButton(
                            onClick = {}
                        ) {
                            Text(
                                text = "Clear all",
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onSurface,
                            )
                        }
                        OutlinedButton(
                            onClick = {}
                        ) {
                            Text(
                                text = "Run all",
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onSurface,
                            )
                        }
                    },
                    modifier = Modifier
                ) {
                    AnimatedVisibility(queued.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No videos queued",
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
                        queued.forEach { video ->
                            ProcessVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                displayMessage = displayMessage,
                                onDeleteClicked = {
                                    vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(video.id))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            item {
                SectionContainer(
                    name = "Progress videos: ${progress.size}",
                    headerBgColor = Color.Blue,
                    isOpen = progress.isNotEmpty(),
                    extra = {
                    },
                    modifier = Modifier
                ) {
                    AnimatedVisibility(queued.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No videos in progress",
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
                        progress.forEach { video ->
                            ProcessVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                displayMessage = displayMessage,
                                onDeleteClicked = {
                                    vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(video.id))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            item {
                SectionContainer(
                    name = "Completed videos: ${completed.size}",
                    isOpen = completed.isNotEmpty(),
                    headerBgColor = Color.Green,
                    extra = {
                        AnimatedVisibility(completed.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {}
                            ) {
                                Text(
                                    text = "Clear all",
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }
                        }
                    },
                    modifier = Modifier
                ) {
                    AnimatedVisibility(completed.isEmpty()) {
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
                        completed.forEach { video ->
                            ProcessVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                displayMessage = displayMessage,
                                onDeleteClicked = {
                                    vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(video.id))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            item {
                SectionContainer(
                    name = "Failed videos: ${state.failed.size}",
                    isOpen = state.failed.isNotEmpty(),
                    headerBgColor = Color.Red,
                    extra = {
                        AnimatedVisibility(state.failed.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {}
                            ) {
                                Text(
                                    text = "Clear all",
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }
                        }
                        AnimatedVisibility(state.failed.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {}
                            ) {
                                Text(
                                    text = "Run again",
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }
                        }
                    },
                    modifier = Modifier
                ) {
                    AnimatedVisibility(state.failed.isEmpty()) {
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
                        state.videos.filter { it.id in state.failed }.forEach { video ->
                            ProcessVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                displayMessage = displayMessage,
                                onDeleteClicked = {
                                    vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(video.id))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(layColumnState)
        )
    }
}
