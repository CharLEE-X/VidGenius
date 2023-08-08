package com.charleex.vidgenius.ui.features.generation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Pets
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
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.TopBar
import com.charleex.vidgenius.ui.features.generation.components.CompletedSection
import com.charleex.vidgenius.ui.features.generation.local.LocalSection
import com.charleex.vidgenius.ui.features.generation.yt_video.YtSection
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent

@Composable
fun GenerationContent(
    videoProcessing: VideoProcessing,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()

    val ytVideos by videoProcessing.ytVideos.collectAsState(emptyList())
    val videos by videoProcessing.videos.collectAsState(emptyList())
    val isFetchingUploads by videoProcessing.isFetchingUploads.collectAsState()
    val config by configManager.config.collectAsState()

    val categories = mapOf(
        "Animals" to Icons.Default.Pets,
        "Fails" to Icons.Default.CarCrash,
//        "Hacks" to Icons.Default.LaptopMac,
    )
    var selectedCategory by remember { mutableStateOf("Animals") }

    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        message?.let {
            displayMessage(it)
        }
    }


    val target = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>
                println("Dropped: ${droppedFiles.size}")
                scope.launch {
                    videoProcessing.addVideos(droppedFiles)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    window.contentPane.dropTarget = target

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                top = 64.dp + 24.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                YtSection(
                    ytVideos = ytVideos,
                    videos = videos,
                    isFetchingUploads = isFetchingUploads,
                    onRefresh = {
                        scope.launch {
                            videoProcessing.fetchUploads()
                        }
                    },
                )
            }
            item {
                LocalSection(
                    videos = videos,
                    ytVideos = ytVideos,
                    onDelete = {
                        videoProcessing.deleteVideo(it)
                    },

                    onStartAll = {
                        scope.launch {
                            val items =
                                videos.filter { it.youtubeTitle in ytVideos.map { it.title } }
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
                    videos = videos.filter { it.isCompleted },
                    onDelete = {
                        videoProcessing.deleteVideo(it)
                    }
                )
            }
        }
        TopBar(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.align(Alignment.TopCenter),
        )

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(layColumnState)
        )
    }
}
