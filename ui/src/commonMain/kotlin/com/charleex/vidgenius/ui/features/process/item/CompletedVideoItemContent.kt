package com.charleex.vidgenius.ui.features.process.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.features.process.components.SectionContainer
import com.charleex.vidgenius.ui.util.Breakpoint
import com.charleex.vidgenius.ui.util.pretty

@Composable
internal fun CompletedVideoItemContent(
    modifier: Modifier = Modifier,
    uiVideo: UiVideo,
    breakpoint: Breakpoint,
    onDeleteClicked: () -> Unit,
) {
    SectionContainer(
        name = uiVideo.path,
        openInitially = false,
        extra = {
            Text(
                text = uiVideo.modifiedAt.pretty(),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
            )
            AppOutlinedButton(
                label = "Delete",
                icon = Icons.Default.PlayArrow,
                onClick = onDeleteClicked,
            )
        },
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                uiVideo.screenshots.forEach {
                    LocalImage(
                        filePath = it,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                    ) {}
                }
            }
            Text(
                text = "Descriptions",
                color = MaterialTheme.colors.onSurface,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 16.dp),
            ) {
                uiVideo.descriptions.forEach {
                    Text(
                        text = "- $it",
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
            Text(
                text = "Context: ${uiVideo.descriptionContext}",
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = "Title: ${uiVideo.title}",
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = "Description: ${uiVideo.description}",
                color = MaterialTheme.colors.onSurface,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Tags: ",
                    color = MaterialTheme.colors.onSurface,
                )
                uiVideo.tags.forEach {
                    Text(
                        text = it,
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
            Text(
                text = "Youtube Video Id: ${uiVideo.youtubeVideoId}",
                color = MaterialTheme.colors.onSurface,
            )
        }
    }
}
