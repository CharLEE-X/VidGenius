package com.charleex.vidgenius.ui.features.process

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.youtube.ChannelsManager
import com.charleex.vidgenius.datasource.feature.youtube.model.ytChannels
import com.charleex.vidgenius.ui.components.AppScaffold
import com.charleex.vidgenius.ui.components.KXSnackBarHost
import com.charleex.vidgenius.ui.features.process.section.components.HeaderWithChannelChooser
import com.charleex.vidgenius.ui.features.process.section.components.CompletedSection
import com.charleex.vidgenius.ui.features.process.section.local.LocalSection
import com.charleex.vidgenius.ui.features.process.section.yt_video.YtSection
import com.charleex.vidgenius.ui.theme.AutoYtVidTheme
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent

@Composable
fun ProcessVideosContent(
    videoProcessing: VideoProcessing,
    channelsManager: ChannelsManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val layColumnState = rememberLazyListState()

    val ytVideos by videoProcessing.ytVideos.collectAsState(emptyList())
    val videos by videoProcessing.videos.collectAsState(emptyList())
    val isFetchingUploads by videoProcessing.isFetchingUploads.collectAsState()
    val config by channelsManager.config.collectAsState()

    val darkLightImage = if (isSystemInDarkTheme())
        "bg/bg_dark.png" else "bg/bg_light.png"

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

    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    AutoYtVidTheme {
        BoxWithConstraints(
            modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .onSizeChanged {
                    com.charleex.vidgenius.ui.AppState.windowSize.value =
                        com.charleex.vidgenius.ui.AppState.windowSize.value.copy(
                            width = it.width.dp,
                            height = it.height.dp
                        )
                }
        ) {
            Image(
                painter = painterResource(darkLightImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .blur(100.dp)
                    .fillMaxSize()
            )
            AppScaffold {
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
                                HeaderWithChannelChooser(
                                    channels = ytChannels,
                                    selectedChannelid = config.channelId,
                                    onChannelSelected = {
                                        scope.launch {
                                            channelsManager.chooseChannel(it)
                                        }
                                    },
                                )
                        }
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
                                        val items = videos.filter { it.youtubeId in ytVideos.map { it.title } }
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

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(layColumnState)
                    )
                }
            }

            KXSnackBarHost(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
