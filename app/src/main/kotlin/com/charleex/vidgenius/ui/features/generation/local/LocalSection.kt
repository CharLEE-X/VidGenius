package com.charleex.vidgenius.ui.features.generation.local

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.model.LocalVideo
import com.charleex.vidgenius.datasource.model.YtVideo
import com.charleex.vidgenius.ui.components.SectionContainer

@Composable
fun LocalSection(
    videos: List<LocalVideo>,
    ytVideos: List<YtVideo>,
    onStartAll: () -> Unit,
    onStartOne: (LocalVideo) -> Unit,
    onDelete: (String) -> Unit,
) {
    SectionContainer(
        name = "Local videos: ${videos.size}",
        isMainHeader = true,
        extra = {
//            AnimatedVisibility(
//                visible = videos.isNotEmpty(),
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                AppOutlinedButton(
//                    label = "Delete All",
//                    icon = Icons.Default.PlayArrow,
//                    onClick = {
//                        videos.forEach {
//                            onDelete(it.id)
//                        }
//                    },
//                )
//            }
            AnimatedVisibility(
                visible = videos.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
//                AppOutlinedButton(
//                    label = "Start All",
//                    icon = Icons.Default.PlayArrow,
//                    onClick = onStartAll,
//                )
            }
        },
    ) {
        AnimatedVisibility(videos.none { !it.isCompleted }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No local videos.",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(64.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            videos
                .filter { !it.isCompleted }
                .forEach { video ->
//                    val isOnYTw
                }
        }
    }
}
