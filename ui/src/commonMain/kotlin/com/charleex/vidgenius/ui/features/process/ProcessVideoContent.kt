package com.charleex.vidgenius.ui.features.process

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.charleex.vidgenius.feature.process_video.ProcessVideoViewModel
import com.charleex.vidgenius.ui.features.process.section.DescriptionContent
import com.charleex.vidgenius.ui.features.process.section.DragContent
import com.charleex.vidgenius.ui.features.process.section.MetaDataContent
import com.charleex.vidgenius.ui.features.process.section.ScreenshotsContent
import com.charleex.vidgenius.ui.features.process.section.UploadContent
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun DragDropContent(
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideoViewModel(
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
                DragContent(
                    window = window,
                    vm = vm,
                    state = state,
                    modifier = Modifier
                )
            }
            item {
                ScreenshotsContent(
                    vm = vm,
                    state = state,
                    modifier = Modifier
                )
            }
            item {
                DescriptionContent(
                    vm = vm,
                    state = state,
                    modifier = Modifier
                )
            }
            item {
                MetaDataContent(
                    vm = vm,
                    state = state,
                    modifier = Modifier
                )
            }
            item {
                UploadContent(
                    vm = vm,
                    state = state,
                    modifier = Modifier
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
