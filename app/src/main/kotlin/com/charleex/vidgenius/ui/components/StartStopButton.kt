package com.charleex.vidgenius.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun StartStopButton(
    startLabel: String,
    stopLabel: String,
    onStart: () -> Unit,
    onStop: () -> Unit,
    isStarted: Boolean,
    width: Dp = 160.dp,
    height: Dp = 40.dp,
    enabled: Boolean = true,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isStarted)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    )
    FilledTonalButton(
        shape = CutCornerShape(8.dp),
        enabled = enabled,
        onClick = {
            if (isStarted) onStop() else onStart()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        modifier = Modifier
            .width(width)
            .height(height)
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
