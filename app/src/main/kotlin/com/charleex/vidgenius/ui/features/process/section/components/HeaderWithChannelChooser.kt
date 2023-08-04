package com.charleex.vidgenius.ui.features.process.section.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.feature.youtube.model.YtChannel

@Composable
fun HeaderWithChannelChooser(
    channels: List<YtChannel>,
    selectedChannelid: String?,
    onChannelSelected: (YtChannel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = channels.firstOrNull { it.id == selectedChannelid }?.title ?: "No channel selected",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
//                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, (10).dp)
            ) {
                channels.forEach {
                    val isCurrent = it.id == selectedChannelid
                    val title = if (isCurrent)
                        "${it.title} (current)" else it.title
                    DropdownMenuItem(
                        enabled = !isCurrent,
                        onClick = {
                            onChannelSelected(it)
                            expanded = false
                        },
                        content = {
                            Text(title)
                        },
                    )
                }
            }
        }
    }
}
