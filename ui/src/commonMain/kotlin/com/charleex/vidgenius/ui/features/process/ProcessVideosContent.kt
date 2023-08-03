package com.charleex.vidgenius.ui.features.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import com.charleex.vidgenius.feature.process_videos.Category
import com.charleex.vidgenius.feature.process_videos.ProcessVideosContract
import com.charleex.vidgenius.feature.process_videos.ProcessVideosViewModel
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.DragArea
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.features.process.item.CompletedVideoItemContent
import com.charleex.vidgenius.ui.features.process.item.QueuedVideoItemContent
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
    val layColumnState = rememberLazyListState()

    var queued by remember { mutableStateOf(listOf<UiVideo>()) }
    var completed by remember { mutableStateOf(listOf<UiVideo>()) }

    LaunchedEffect(state.videos) {
        queued = state.videos.filter {
            if (state.uploadYouTube)
                !it.hasYoutubeVideoId() else !it.hasMetadata()
        }
            .sortedBy { it.modifiedAt }

        completed = state.videos
            .filter {
                if (state.uploadYouTube)
                    it.hasYoutubeVideoId() else it.hasMetadata()
            }
            .sortedBy { it.modifiedAt }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                SectionContainer(
                    name = "Drag and drop videos",
                    headerBgColor = Color.Magenta,
                    isMainHeader = true,
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
                    isMainHeader = true,
                    extra = {
                        AnimatedVisibility(
                            visible = queued.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                AppOutlinedButton(
                                    label = "Clear All",
                                    icon = Icons.Default.Delete,
                                    onClick = {
                                        queued.forEach {
                                            vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(it.id))
                                        }
                                    },
                                )
                                AppOutlinedButton(
                                    label = "Start All",
                                    icon = Icons.Default.PlayArrow,
                                    onClick = {
                                        queued.forEach {
                                            vm.trySend(
                                                ProcessVideosContract.Inputs.StartVideoProcessing(
                                                    it.id
                                                )
                                            )
                                        }
                                    },
                                )
                            }
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
                        AnimatedVisibility(queued.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                var categoryDropdownOpen by remember { mutableStateOf(false) }
                                Box {
                                    AppOutlinedButton(
                                        label = "Category",
                                        icon = Icons.Default.Category,
                                        onClick = {
                                            categoryDropdownOpen = !categoryDropdownOpen
                                        },
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownOpen,
                                        onDismissRequest = { categoryDropdownOpen = false },
                                    ) {
                                        Category.values().forEach { category ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    categoryDropdownOpen = false
                                                    vm.trySend(
                                                        ProcessVideosContract.Inputs.SetCategory(
                                                            category
                                                        )
                                                    )
                                                }
                                            ) {
                                                Surface {
                                                    Text(text = category.name)
                                                }
                                            }
                                        }
                                    }
                                }

                                var screenshotsDropdownOpen by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = null,
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colors.primary,
                                        ),
                                        onClick = {
                                            screenshotsDropdownOpen = !screenshotsDropdownOpen
//                                        vm.trySend(ProcessVideosContract.Inputs.SetCategory(Category.ANIMALS))
                                        },
                                    ) {
                                        Text(
                                            text = "Screenshots:",
                                            style = MaterialTheme.typography.button,
                                            color = MaterialTheme.colors.onSurface,
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text(
                                            text = state.numberOfScreenshots.toString(),
                                            style = MaterialTheme.typography.button,
                                            color = MaterialTheme.colors.onSurface,
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = screenshotsDropdownOpen,
                                        onDismissRequest = { screenshotsDropdownOpen = false },
                                    ) {
                                        (1..7).forEach { number ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    screenshotsDropdownOpen = false
                                                    vm.trySend(
                                                        ProcessVideosContract.Inputs.SetNumberOfScreenshots(
                                                            number
                                                        )
                                                    )
                                                }
                                            ) {
                                                Surface {
                                                    Text(text = number.toString())
                                                }
                                            }
                                        }
                                    }
                                }

                                val ytColor by animateColorAsState(
                                    if (state.uploadYouTube) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                                )
                                AppOutlinedButton(
                                    label = "YT Upload",
                                    icon = if (state.uploadYouTube) Icons.Default.Check else Icons.Default.Close,
                                    bgColor = ytColor,
                                    labelColor = Color.White,
                                    onClick = {
                                        vm.trySend(
                                            ProcessVideosContract.Inputs.SetUploadYouTube(
                                                state.uploadYouTube.not()
                                            )
                                        )
                                    },
                                )
                            }
                        }
                        queued.forEach { video ->
                            QueuedVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                youtubeUploadOn = state.uploadYouTube,
                                onDeleteClicked = {
                                    vm.trySend(
                                        ProcessVideosContract.Inputs.CancelProcessingVideo(
                                            video.id
                                        )
                                    )
                                    vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(video.id))
                                },
                                onStartProcessingClicked = {
                                    vm.trySend(
                                        ProcessVideosContract.Inputs.StartVideoProcessing(
                                            video.id
                                        )
                                    )
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
                    headerBgColor = Color.Green,
                    isMainHeader = true,
                    extra = {
                        AnimatedVisibility(completed.isNotEmpty()) {
                            AppOutlinedButton(
                                label = "Clear All",
                                icon = Icons.Default.Delete,
                                onClick = {
                                    completed.forEach {
                                        vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(it.id))
                                    }
                                },
                            )
                        }
                    },
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
                            CompletedVideoItemContent(
                                uiVideo = video,
                                breakpoint = breakpoint,
                                onDeleteClicked = { // Not used
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
