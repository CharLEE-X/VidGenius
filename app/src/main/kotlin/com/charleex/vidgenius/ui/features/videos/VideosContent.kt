package com.charleex.vidgenius.ui.features.videos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.DropTarget
import com.charleex.vidgenius.ui.components.list.AppListItem
import com.charleex.vidgenius.ui.components.list.ListHeaderYT
import com.charleex.vidgenius.ui.components.list.NoVideos
import kotlinx.coroutines.launch

@Composable
fun VideosContent(
    videoService: VideoService,
    configManager: ConfigManager,
    window: ComposeWindow,
    displayMessage: (String) -> Unit,
    onItemClicked: (String) -> Unit,
    scroll: (LazyListState) -> Unit,
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

    LaunchedEffect(layColumnState) {
        scroll(layColumnState)
    }

    DropTarget(
        window = window,
        onDropped = { files ->
            scope.launch {
                videoService.addLocalVideos(files)
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    top = 100.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                )
            )
    ) {
        ListHeaderYT(
            videoService = videoService,
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
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                NoVideos(videos.isEmpty())
            }
            items(videos) { video ->
                AppListItem(
                    video = video,
                    videoService = videoService,
                    onClick = onItemClicked,
                )
            }
        }
    }
}
