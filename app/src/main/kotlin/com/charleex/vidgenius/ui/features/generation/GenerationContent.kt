package com.charleex.vidgenius.ui.features.generation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.AppVerticalScrollbar
import com.charleex.vidgenius.ui.components.CounterAnimation
import com.charleex.vidgenius.ui.components.NoVideos
import com.charleex.vidgenius.ui.components.TopBar
import com.charleex.vidgenius.ui.util.pretty
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
                top = 128.dp,
                start = 32.dp,
                end = 32.dp,
                bottom = 32.dp
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Surface {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row {
                            Text(
                                text = "Total:",
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CounterAnimation(
                                count = ytVideos.size,
                            ) {
                                Text(
                                    text = it.toString(),
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                scope.launch {
                                    videoProcessing.fetchUploads()
                                }
                            }
                        ) {
                            Text(
                                text = "Add videos",
                            )
                        }
                    }
                }
            }
            item {
                NoVideos(ytVideos.isEmpty())
            }
            items(ytVideos) { ytVideo ->
                ListItem(
                    headlineContent = {
                        Text(ytVideo.title)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null
                        )
                    },
                    overlineContent = {
                        Text(ytVideo.publishedAt.pretty())
                    },
                    supportingContent = {
                        Text(ytVideo.privacyStatus ?: "Not known")
                    },
                    trailingContent = {
                        Text(ytVideo.tags.joinToString(" "))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
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
