package com.charleex.vidgenius.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.videodetail.VideoDetailViewModel
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun VideoDetailContent(
    videoId: String,
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        VideoDetailViewModel(
            scope = scope,
            videoId = videoId,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()

    AppCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        AnimatedVisibility(state.loading) {
            CircularProgressIndicator()
        }
        AnimatedVisibility(!state.loading) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row {
                    ItemText(
                        text = state.videoDetail.id,
                    )
                    AppFlexSpacer()
                    ItemText(
                        text = state.videoDetail.publishedAt,
                    )
                }
                ItemText(
                    text = state.videoDetail.title,
                )
                ItemText(
                    text = state.videoDetail.description,
                    maxLines = 3,
                )
            }
        }
    }
}
