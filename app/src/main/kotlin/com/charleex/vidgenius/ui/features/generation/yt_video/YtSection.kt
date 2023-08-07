package com.charleex.vidgenius.ui.features.generation.yt_video

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.ui.components.SectionContainer
import com.charleex.vidgenius.ui.features.uploads.AppFilledButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YtSection(
    ytVideos: List<YtVideo>,
    videos: List<Video>,
    isFetchingUploads: Boolean,
    onRefresh: () -> Unit,
) {
    SectionContainer(
        name = "Youtube 'Draft' videos: ${ytVideos.size}",
        isMainHeader = true,
        enabled = ytVideos.isNotEmpty(),
        openInitially = ytVideos.isNotEmpty(),
        extra = {
            AppFilledButton(
                label = "Refresh",
                imageVector = Icons.Default.Refresh,
                isLoading = isFetchingUploads,
                onClick = onRefresh,
            )
        },
        modifier = Modifier
    ) {
        Surface(
            tonalElevation = 2.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                ytVideos.forEachIndexed { index, ytVideo ->
                    val videoYtNames = videos.map { it.youtubeName }
                    val isFoundLocally = ytVideo.title in videoYtNames

                    ListItem(
                        headlineText = { Text(text = ytVideo.title) },
                        trailingContent = {
                            Text(text = if (isFoundLocally) "Found" else "Not found")
                        },
                    )
                    if (index != ytVideos.size - 1) {
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
