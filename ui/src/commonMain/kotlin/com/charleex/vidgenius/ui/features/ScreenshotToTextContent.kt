package com.charleex.vidgenius.ui.features

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.videoscreenshots.ScreenshotsToTextViewModel
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun ScreenshotToTextContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    videoId: String,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ScreenshotsToTextViewModel(
            scope = scope,
            showMessage = displayMessage,
            videoId = videoId
        )
    }
    val state by vm.observeStates().collectAsState()
    val layColumnState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = layColumnState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                top = 32.dp,
                bottom = 32.dp,
            ),
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            item {
                Row {
                Text(
                    text = state.video.path,
                    color = MaterialTheme.colors.onSurface,
                )
                    Text(
                        text = state.progress.toString(),
                        color = MaterialTheme.colors.onSurface,
                    )
                    Text(
                        text = state.processing.toString(),
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.video.screenshots.forEach { screenshot ->
                        Text(
                            text = screenshot.path,
                            color = MaterialTheme.colors.onSurface,
                        )
                        screenshot.description?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colors.onSurface,
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
