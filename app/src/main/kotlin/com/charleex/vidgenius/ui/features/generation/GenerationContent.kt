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
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.AppVerticalScrollbar
import com.charleex.vidgenius.ui.components.DropTarget
import com.charleex.vidgenius.ui.components.TopBar
import com.charleex.vidgenius.ui.components.list.ListHeader
import com.charleex.vidgenius.ui.components.list.NoVideos
import kotlinx.coroutines.launch

@Composable
fun GenerationContent(
    videoService: VideoService,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()

    val isFetchingUploads by videoService.isFetchingUploads.collectAsState()
    val videos by videoService.videos.collectAsState(emptyList())
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
                videoService.addLocalVideos(files)
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
                        count = videos.size,
                        isRefreshing = isFetchingUploads,
                        startRefresh = {
                            scope.launch {
                                videoService.startFetchingUploads()
                            }
                        },
                        stopRefresh = {
                            videoService.stopFetchingUploads()
                        },
                        selectedPrivacyStatuses = selectedPrivacyStatuses,
                        onPrivacySelected = {
                            configManager.setPrivacyStatus(it)
                        }
                    )
                }
                item {
                    NoVideos(videos.isEmpty())
                }
                items(videos) { videos ->
//                    AppListItem(
//                        title = videos.title,
//                        thumbnailUrl = videos.thumbnailUrl,
//                        privacyStatus = videos.privacyStatus,
//                        publishedAt = videos.publishedAt.pretty(),
//                    )
                }
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
