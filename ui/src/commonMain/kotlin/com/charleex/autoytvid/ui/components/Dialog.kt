package com.charleex.autoytvid.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun Dialog(
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    buttons: @Composable () -> Unit = {
        AppButton(
            label = { Text("Open") },
            onClick = {
                onDismiss()
            },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        )
        AppButton(
            label = { Text("Cancel") },
            onClick = onDismiss,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        )
    },
) {
    if (show) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    color = MaterialTheme.colors.primary,
                ),
                elevation = 20.dp,
                modifier = Modifier
                    .fillMaxWidth(.8f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    title()
                    text()
                    buttons()
                }
            }
        }
    }
}
