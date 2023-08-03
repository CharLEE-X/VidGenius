package com.charleex.vidgenius.ui.features.process.section.yt_video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.SectionContainer

@Composable
fun YtSection(
    ytVideos: List<YtVideo>,
    videos: List<Video>,
    onRefresh: () -> Unit,
) {
    SectionContainer(
        name = "Youtube 'Draft' videos: ${ytVideos.size}",
        headerBgColor = Color.LightGray,
        isMainHeader = true,
        extra = {
            AppOutlinedButton(
                label = "Refresh Drafts",
                icon = Icons.Default.PlayArrow,
                onClick = onRefresh,
            )
        },
        modifier = Modifier
    ) {
        AnimatedVisibility(ytVideos.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No YouTube 'Draft' videos",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
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
            ytVideos.forEach { ytVideo ->
                val videoYtIds = videos.map { it.youtubeId }
                val isFoundLocally = ytVideo.title in videoYtIds
                println("isFoundLocally: $isFoundLocally, ytVideo: ${ytVideo.title}, videos: $videoYtIds")
                YtVideoItem(
                    ytVideo = ytVideo,
                    isFoundLocally = videos.any { it.youtubeId == ytVideo.title },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
