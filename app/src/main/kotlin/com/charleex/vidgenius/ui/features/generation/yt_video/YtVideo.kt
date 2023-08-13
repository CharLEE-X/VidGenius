package com.charleex.vidgenius.ui.features.generation.yt_video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.model.YtVideo

@Composable
internal fun YtVideoItem(
    modifier: Modifier = Modifier,
    ytVideo: YtVideo,
    isFoundLocally: Boolean,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(
                            start = 48.dp,
                            end = 64.dp,
                        )
                ) {
                    Text(
                        text = "${ytVideo.title} | ${ytVideo.id}",
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedVisibility(isFoundLocally) {
                        Text(
                            text = "Found locally",
                        )
                    }
                    AnimatedVisibility(!isFoundLocally) {
                        Text(
                            text = "Not found locally",
                        )
                    }
                }
            }
        }
    }
}
