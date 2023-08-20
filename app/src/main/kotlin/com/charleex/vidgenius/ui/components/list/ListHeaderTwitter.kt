package com.charleex.vidgenius.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.ui.components.CounterAnimation
import com.charleex.vidgenius.ui.components.StartStopButton

@Composable
internal fun ListHeaderTwitter(
    title: String,
    count: Int,
    isRefreshing: Boolean,
    startRefresh: () -> Unit,
    stopRefresh: () -> Unit,
) {
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

