package com.charleex.vidgenius.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.privacyStatusFromString
import com.charleex.vidgenius.ui.components.CounterAnimation
import com.charleex.vidgenius.ui.components.SegmentDefaults
import com.charleex.vidgenius.ui.components.SegmentSpec
import com.charleex.vidgenius.ui.components.SegmentsGroup
import com.charleex.vidgenius.ui.components.StartStopButton

@Composable
internal fun ListHeaderYT(
    videoService: VideoService,
    title: String,
    count: Int,
    isRefreshing: Boolean,
    startRefresh: () -> Unit,
    stopRefresh: () -> Unit,
    selectedPrivacyStatuses: List<PrivacyStatus>,
    onPrivacySelected: (PrivacyStatus) -> Unit,
) {
    val selectedVideos by videoService.selectedVideos.collectAsState()
    val isGenerating by videoService.isGenerating.collectAsState()

    val segmentsList = PrivacyStatus.values().map {
        val icon = when (it) {
            PrivacyStatus.PUBLIC -> Icons.Default.Visibility
            PrivacyStatus.UNLISTED -> Icons.Default.Pending
            PrivacyStatus.PRIVATE -> Icons.Default.VisibilityOff
        }
        SegmentSpec(
            label = it.value,
            icon = icon,
            colors = SegmentDefaults.defaultColors(),
            corners = SegmentDefaults.defaultCorners(),
        )
    }
    val selectedIndexes = selectedPrivacyStatuses.map { it.ordinal }

    val onSegmentClicked = { index: Int ->
        val privacyString = segmentsList[index].label
        val privacyStatus = privacyStatusFromString(privacyString)
        onPrivacySelected(privacyStatus)
    }

    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Total:",
                style = MaterialTheme.typography.bodyLarge,
            )
            CounterAnimation(
                count = count,
            ) {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SegmentsGroup(
                segments = segmentsList,
                selectedIndexes = selectedIndexes,
                onSegmentClicked = onSegmentClicked,
                segmentModifier = Modifier
                    .height(40.dp),
                modifier = Modifier
                    .width(360.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StartStopButton(
                startLabel = "${selectedVideos.size} Generate",
                stopLabel = "${selectedVideos.size} Generating...",
                onStart = {
                    videoService.generateAndUpdateSelectedAsync()
                },
                onStop = {},
                isStarted = isGenerating,
                enabled = selectedVideos.isNotEmpty(),
            )
            StartStopButton(
                startLabel = "Refresh",
                stopLabel = "Stop",
                onStart = startRefresh,
                onStop = stopRefresh,
                isStarted = isRefreshing,
            )
        }
    }
}
