package com.charleex.vidgenius.ui.features.process.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.charleex.vidgenius.feature.process_video.model.ProgressState

@Composable
internal fun ArrowDown(
    progressState: ProgressState,
) {
    val colorState by animateColorAsState(
        when (progressState) {
            ProgressState.None -> Color.Transparent
            ProgressState.Cancelled,
            ProgressState.Queued -> Color.LightGray
            is ProgressState.InProgress -> Color.Gray
            is ProgressState.Success -> Color.Green
            is ProgressState.Error -> Color.Red
        }
    )
    Icon(
        imageVector = Icons.Default.ArrowDownward,
        contentDescription = "Arrow Downward",
        tint = colorState,
        modifier = Modifier
    )
}
