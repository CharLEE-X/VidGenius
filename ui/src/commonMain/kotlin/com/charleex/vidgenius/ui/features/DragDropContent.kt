package com.charleex.vidgenius.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.dragdrop.DragDropContract
import com.charleex.vidgenius.feature.dragdrop.model.DragDropItem
import com.charleex.vidgenius.feature.dragdrop.DragDropViewModel
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.components.DragArea
import com.charleex.vidgenius.ui.components.InfoText
import com.charleex.vidgenius.ui.util.Breakpoint
import com.charleex.vidgenius.ui.util.pretty

@Composable
internal fun DragDropContent(
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    window: ComposeWindow,
    goToVideoScreenshots: (id: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        DragDropViewModel(
            scope = scope,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()

    AppCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()

    ) {
        Column {
            DragArea(
                window = window,
                onDropped = {files ->
                    vm.trySend(DragDropContract.Inputs.GetFiles(files))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
            DroppedFiles(
                files = state.dragDropItems,
                goToVideoScreenshots = goToVideoScreenshots,
                onDelete = {
                    vm.trySend(DragDropContract.Inputs.DeleteFile(it))
                }
            )
        }
    }
}

@Composable
internal fun DroppedFiles(
    files: List<DragDropItem>,
    goToVideoScreenshots: (id: String) -> Unit,
    onDelete: (DragDropItem) -> Unit,
) {
    AnimatedVisibility(visible = files.isEmpty()) {
        InfoText(
            text = "No files dropped",
            modifier = Modifier
                .padding(24.dp)
        )
    }
    if (files.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(files) { dragDropItem ->
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    visible = true
                }
                AnimatedVisibility(visible) {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 24.dp
                                ),
                        ) {
                            Text(
                                text = dragDropItem.path,
                                color = MaterialTheme.colors.onSurface,
                            )
                            Text(
                                text = dragDropItem.modifiedAt.pretty(),
                                color = MaterialTheme.colors.onSurface,
                            )
                            AppFlexSpacer()
                            Button(
                                onClick = { goToVideoScreenshots(dragDropItem.id) },
                            ) {
                                Text(
                                    text = "Screenshots",
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .selectable(
                                        selected = false,
                                        onClick = { onDelete(dragDropItem) })
                            )
                        }
                    }
                }
            }
        }
    }
}
