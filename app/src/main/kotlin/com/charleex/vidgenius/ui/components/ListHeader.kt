package com.charleex.vidgenius.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.privacyStatusFromString

@Composable
internal fun ListHeader(
    title: String,
    count: Int,
    isRefreshing: Boolean,
    startRefresh: () -> Unit,
    stopRefresh: () -> Unit,
    selectedPrivacyStatuses: List<PrivacyStatus>,
    onPrivacySelected: (PrivacyStatus) -> Unit,
) {
    val segmentsList = PrivacyStatus.values().map {
        SegmentSpec(
            label = it.value,
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
                .padding(24.dp)
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
                modifier = Modifier.width(320.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
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

@Composable
private fun StartStopButton(
    startLabel: String,
    stopLabel: String,
    onStart: () -> Unit,
    onStop: () -> Unit,
    isStarted: Boolean,
    width: Dp = 160.dp,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isStarted)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    )
    FilledTonalButton(
        onClick = {
            if (isStarted) onStop() else onStart()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        modifier = Modifier.width(width)
    ) {
        if (isStarted) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp),
            )
        } else {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(if (isStarted) 24.dp else 8.dp))
        Text(
            text = if (isStarted) stopLabel else startLabel,
        )
    }
}
