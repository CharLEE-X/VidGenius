package com.charleex.vidgenius.ui.features.process

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video.ProcessVideosContract
import com.charleex.vidgenius.feature.process_video.ProcessVideosViewModel
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
    val layColumnState = rememberLazyListState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp)
        ) {
            item {
                SectionContainer(
                    name = "Drag and drop videos",
                    isOpen = state.videos.isEmpty(),
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
                            .padding(32.dp)
                    )
                }
            }
            items(state.videos) { videoId ->
                ProcessVideoItemContent(
                    videoId = videoId,
                    breakpoint = breakpoint,
                    displayMessage = displayMessage,
                    onDeleteClicked = {
                        vm.trySend(ProcessVideosContract.Inputs.DeleteVideoId(videoId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
//            item {
//                ScreenshotsContent(
//                    vm = vm,
//                    state = state,
//                    modifier = Modifier
//                )
//            }
//            item {
//                DescriptionContent(
//                    vm = vm,
//                    state = state,
//                    modifier = Modifier
//                )
//            }
//            item {
//                MetaDataContent(
//                    vm = vm,
//                    state = state,
//                    modifier = Modifier
//                )
//            }
//            item {
//                UploadContent(
//                    vm = vm,
//                    state = state,
//                    modifier = Modifier
//                )
//            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(layColumnState)
        )
    }
}
