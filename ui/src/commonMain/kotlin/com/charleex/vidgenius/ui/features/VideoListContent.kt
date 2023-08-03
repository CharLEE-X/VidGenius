package com.charleex.vidgenius.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.videolist.VideoListItem
import com.charleex.vidgenius.feature.videolist.VideoListViewModel
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.util.Breakpoint
import com.charleex.vidgenius.ui.util.pretty

@Composable
internal fun VideoListContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    goToVideoDetail: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        VideoListViewModel(
            scope = scope,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()
    val layColumnState = rememberLazyListState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(state.list.isEmpty() && !state.showLoader) {
                Text(text = "No uploads")
            }
            AnimatedVisibility(state.showLoader) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(!state.showLoader) {
                LazyColumn(
                    contentPadding = PaddingValues(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = layColumnState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(state.list) { videoListItem ->
                        VideoListItem(
                            item = videoListItem,
                            onClick = { goToVideoDetail(videoListItem.id) }
                        )
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

@Composable
fun VideoListItem(
    item: VideoListItem,
    onClick: () -> Unit,
) {
    AppCard(
        modifier = Modifier
            .clickable {
                onClick()
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {

                ItemText(
                    text = item.id,
                )
                AppFlexSpacer()
                ItemText(
                    text = item.publishedAt.pretty(),
                )
            }
            ItemText(
                text = item.title,
            )
            item.description?.let {
                ItemText(
                    text = it,
                    maxLines = 3,
                )
            }
        }
    }
}

@Composable
internal fun ItemText(
    text: String,
    maxLines: Int = 1,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
