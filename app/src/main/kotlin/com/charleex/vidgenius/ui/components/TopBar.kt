package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    onMenuClicked: () -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onMenuClicked()
                    }
            )
            Text(
                text = "VidGenius",
                style = MaterialTheme.typography.displayLarge,
            )
        }
    }
}
