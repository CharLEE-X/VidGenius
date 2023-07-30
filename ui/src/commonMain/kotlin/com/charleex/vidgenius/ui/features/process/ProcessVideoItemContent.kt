package com.charleex.vidgenius.ui.features.process

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Start
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video.ProcessVideoItemContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoItemViewModel
import com.charleex.vidgenius.feature.process_video.model.UiVideoCategory
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun ProcessVideoItemContent(
    modifier: Modifier = Modifier,
    videoId: String,
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideoItemViewModel(
            scope = scope,
            videoId = videoId,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 64.dp
                    ),
            ) {
                Text(
                    text = state.uiVideo.id,
                    color = MaterialTheme.colors.onSurface,
                )
                AppFlexSpacer()
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .selectable(
                            selected = false,
                            onClick = onDeleteClicked
                        )
                )
                Spacer(modifier = Modifier.padding(24.dp))
                FloatingActionButton(
                    onClick = { vm.trySend(ProcessVideoItemContract.Inputs.Video.StartVideoProcessing) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Start,
                        contentDescription = "Start Video Processing",
                    )
                }
            }
            var dropdownMenuExpanded by remember { mutableStateOf(false) }
            val categories = listOf(
                UiVideoCategory(
                    id = "1",
                    name = "Animals",
                ),
                UiVideoCategory(
                    id = "2",
                    name = "Cars",
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Category",
                    color = MaterialTheme.colors.onSurface,
                )
                OutlinedButton(
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                    onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
                    modifier = Modifier
                ) {
                    Text(
                        text = state.category.name,
                        color = MaterialTheme.colors.onSurface,
                    )
                }
                DropdownMenu(
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = { dropdownMenuExpanded = false },
                    offset = DpOffset(74.dp, 0.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                vm.trySend(
                                    ProcessVideoItemContract.Inputs.Config.SetCategory(category)
                                )
                                dropdownMenuExpanded = false
                            }
                        ) {
                            Text(
                                text = category.name,
                                color = MaterialTheme.colors.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}
