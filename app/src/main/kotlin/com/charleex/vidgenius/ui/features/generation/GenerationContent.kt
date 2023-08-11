package com.charleex.vidgenius.ui.features.generation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import com.charleex.vidgenius.ui.components.AppVerticalScrollbar
import com.charleex.vidgenius.ui.components.DropTarget
import com.charleex.vidgenius.ui.components.TopBar
import com.charleex.vidgenius.ui.components.list.AppListItem
import com.charleex.vidgenius.ui.components.list.ListHeader
import com.charleex.vidgenius.ui.components.list.NoVideos
import com.charleex.vidgenius.ui.util.pretty
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image
import java.net.URL

@Composable
fun GenerationContent(
    videoProcessing: VideoProcessing,
    youtubeRepository: YoutubeRepository,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()

    val isFetchingUploads by youtubeRepository.isFetchingUploads.collectAsState()
    val ytVideos by youtubeRepository.ytVideos.collectAsState(emptyList())
    val videos by videoProcessing.videos.collectAsState(emptyList())
    val config by configManager.config.collectAsState()
    val selectedPrivacyStatuses = config.selectedPrivacyStatuses

    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        message?.let {
            displayMessage(it)
        }
    }

    DropTarget(
        window = window,
        onDropped = { files ->
            scope.launch {
                videoProcessing.addVideos(files)
            }
        }
    )
    Surface(
        tonalElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = layColumnState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(
                    top = 100.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    ListHeader(
                        title = "YouTube videos",
                        count = ytVideos.size,
                        isRefreshing = isFetchingUploads,
                        startRefresh = {
                            youtubeRepository.startFetchUploads()
                        },
                        stopRefresh = {
                            youtubeRepository.stopFetchUploads()
                        },
                        selectedPrivacyStatuses = selectedPrivacyStatuses,
                        onPrivacySelected = {
                            configManager.setPrivacyStatus(it)
                        }
                    )
                }
                item {
                    NoVideos(ytVideos.isEmpty())
                }
                items(ytVideos) { ytVideo ->
                    AppListItem(
                        title = ytVideo.title,
                        thumbnailUrl = ytVideo.thumbnailUrl,
                        privacyStatus = ytVideo.privacyStatus,
                        publishedAt = ytVideo.publishedAt.pretty(),
                    )
                }


//            item {
//                YtSection(
//                    ytVideos = ytVideos,
//                    videos = videos,
//                    isFetchingUploads = isFetchingUploads,
//                    onRefresh = {
//                        scope.launch {
//                            videoProcessing.fetchUploads()
//                        }
//                    },
//                )
//            }
//            item {
//                LocalSection(
//                    videos = videos,
//                    ytVideos = ytVideos,
//                    onDelete = {
//                        videoProcessing.deleteVideo(it)
//                    },
//
//                    onStartAll = {
//                        scope.launch {
//                            val items =
//                                videos.filter { it.youtubeTitle in ytVideos.map { it.title } }
//                            videoProcessing.processAll(items) {
//                                message = it
//                            }
//                        }
//                    },
//                    onStartOne = {
//                        val items = listOf(it)
//                        videoProcessing.processAll(items) {
//                            message = it
//                        }
//                    }
//                )
//            }
//            item {
//                CompletedSection(
//                    videos = videos.filter { it.isCompleted },
//                    onDelete = {
//                        videoProcessing.deleteVideo(it)
//                    }
//                )
//            }
            }
            TopBar(
                configManager = configManager,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            AppVerticalScrollbar(
                adapter = rememberScrollbarAdapter(layColumnState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
            )
        }
    }
}

fun loadImage(url: String) =
    Image.makeFromEncoded(URL(url).readBytes())
        .toComposeImageBitmap()
