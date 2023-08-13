package com.charleex.vidgenius.ui.features.generation.yt_video

import androidx.compose.animation.AnimatedVisibility
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
fun YtSection(
    ytVideos: List<YtVideo>,
    videos: List<LocalVideo>,
    isFetchingUploads: Boolean,
    onRefresh: () -> Unit,
) {
    SectionContainer(
        name = "Youtube 'Draft' videos: ${ytVideos.size}",
        isMainHeader = true,
        extra = {
            if (isFetchingUploads) {
//                AppOutlinedButton(
//                    label = "Downloading...",
//                    icon = Icons.Default.CloudDownload,
//                    enabled = false,
//                    onClick = onRefresh,
//                )
            } else {
//                AppOutlinedButton(
//                    label = "Refresh Drafts",
//                    icon = Icons.Default.PlayArrow,
//                    onClick = onRefresh,
//                )
            }
        },
        modifier = Modifier
    ) {
        AnimatedVisibility(ytVideos.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFetchingUploads) "Loading..." else "No YouTube 'Draft' videos",
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
            ytVideos.forEach { ytVideo ->
//                val videoYtIds = videos.map { it.youtubeTitle }
//                val isFoundLocally = ytVideo.title in videoYtIds
//                println("isFoundLocally: $isFoundLocally, ytVideo: ${ytVideo.title}, videos: $videoYtIds")
//                YtVideoItem(
//                    ytVideo = ytVideo,
//                    isFoundLocally = videos.any { it.youtubeTitle == ytVideo.title },
//                    modifier = Modifier.fillMaxWidth()
//                )
            }
        }
    }
}
