package com.charleex.vidgenius.ui.features.generation

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.ConfigManager
import com.charleex.vidgenius.datasource.UploadsManager
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepository
import com.charleex.vidgenius.ui.features.generation.components.CompletedSection
import com.charleex.vidgenius.ui.features.generation.local.LocalSection
import com.charleex.vidgenius.ui.features.generation.yt_video.YtSection
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

@Composable
fun AnimalsGenerationContent(
    videoFileRepository: VideoFileRepository,
    videoProcessing: VideoProcessing,
    uploadsManager: UploadsManager,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    GenerationContent(
        videoFileRepository = videoFileRepository,
        videoProcessing = videoProcessing,
        uploadsManager = uploadsManager,
        configManager = configManager,
        window = window,
        displayMessage = displayMessage,
    )
}

@Composable
fun FailsGenerationContent(
    videoFileRepository: VideoFileRepository,
    videoProcessing: VideoProcessing,
    uploadsManager: UploadsManager,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    GenerationContent(
        videoFileRepository = videoFileRepository,
        videoProcessing = videoProcessing,
        uploadsManager = uploadsManager,
        configManager = configManager,
        window = window,
        displayMessage = displayMessage,
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GenerationContent(
    videoFileRepository: VideoFileRepository,
    videoProcessing: VideoProcessing,
    uploadsManager: UploadsManager,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()

    val ytVideos by uploadsManager.ytVideos.collectAsState(emptyList())
    val videos by videoProcessing.videos.collectAsState(emptyList())
    val isFetchingUploads by uploadsManager.isFetchingUploads.collectAsState()
    val isWatching by videoFileRepository.isWatching.collectAsState()

    val privacyStatuses = listOf("private", "unlisted")
    val ytFiltered = ytVideos.filter { it.privacyStatus in privacyStatuses }

    val localVideos = videos.filter { !it.isCompleted }
    val completedVideos = videos.filter { it.isCompleted }

    var textFieldValue by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        message?.let {
            displayMessage(it)
        }
    }


    window.contentPane.dropTarget = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>
                println("Dropped target: ${droppedFiles.size}")
                scope.launch {
                    val files = droppedFiles.filterIsInstance<File>()
                    val paths = files.map { it.absolutePath }
                    videoProcessing.addVideos(paths)
                    textFieldValue = paths.first()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                top = 48.dp + 24.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        label = { Text("Directory to watch") },
                        modifier = Modifier
                            .weight(1f)
                            .onExternalDrag(
                                onDrop = { externalDragValue ->
                                    val dragData = externalDragValue.dragData
                                    if (dragData is androidx.compose.ui.DragData.FilesList) {
                                    }
                                },
                            )
                    )
                    if (isWatching) {
                        Button(
                            onClick = {
                                scope.launch {
                                    videoFileRepository.stopWatchingDirectory(textFieldValue)
                                }
                            },
                            modifier = Modifier
                                .height(56.dp)
                        ) {
                            Text("Stop watching")
                        }
                    } else {
                        Button(
                            onClick = {
                                scope.launch {
                                    videoFileRepository.startWatchingDirectory(textFieldValue)
                                }
                            },
                            modifier = Modifier
                                .height(56.dp)
                        ) {
                            Text("Start watching")
                        }
                    }
                }
            }
            item {
                YtSection(
                    ytVideos = ytFiltered,
                    videos = videos,
                    isFetchingUploads = isFetchingUploads,
                    onRefresh = {
                        scope.launch {
                            uploadsManager.fetchUploads()
                        }
                    },
                )
            }
            item {
                LocalSection(
                    videos = localVideos,
                    onDelete = {
                        videoProcessing.deleteVideo(it)
                    },

                    onStartAll = {
                        scope.launch {
                            val items = videos.filter { it.youtubeName in ytVideos.map { it.title } }
                            videoProcessing.processAll(items) {
                                message = it
                            }
                        }
                    },
                    onStartOne = {
                        val items = listOf(it)
                        videoProcessing.processAll(items) {
                            message = it
                        }
                    }
                )
            }
            item {
                CompletedSection(
                    videos = completedVideos,
                    onDelete = {
                        videoProcessing.deleteVideo(it)
                    }
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(layColumnState),
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 12.dp,
                shape = RoundedCornerShape(8.dp),
                hoverDurationMillis = 300,
                unhoverColor = Color.Black.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.48f),
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
