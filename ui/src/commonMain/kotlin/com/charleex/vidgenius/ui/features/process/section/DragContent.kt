package com.charleex.vidgenius.ui.features.process.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.dragdrop.ProcessVideoContract
import com.charleex.vidgenius.feature.dragdrop.ProcessVideoViewModel
import com.charleex.vidgenius.feature.dragdrop.model.UiVideo
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.components.DragArea
import com.charleex.vidgenius.ui.features.process.SectionContainer


@Composable
internal fun ColumnScope.DragContent(
    modifier: Modifier = Modifier,
    window: ComposeWindow,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {
    SectionContainer(
        name = "Drag & Drop",
        progressState = state.dragDropState,
        isOpenInitially = true,
        modifier = modifier
    ) {
        state.uiVideo?.let { uiVideo ->
            DroppedItem(
                droppedItem = uiVideo,
                goToVideoScreenshots = {},
                onDelete = {
                    vm.trySend(ProcessVideoContract.Inputs.DragDrop.DeleteFile(it))
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        } ?: run {
            DragArea(
                window = window,
                onDropped = { files ->
                    vm.trySend(ProcessVideoContract.Inputs.DragDrop.InitVideo(files))
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(200.dp)
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }
    }
}

@Composable
internal fun DroppedItem(
    modifier: Modifier = Modifier,
    droppedItem: UiVideo,
    goToVideoScreenshots: (id: String) -> Unit,
    onDelete: (UiVideo) -> Unit,
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
            text = droppedItem.path,
            color = MaterialTheme.colors.onSurface,
        )
        AppFlexSpacer()
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            modifier = Modifier
                .selectable(
                    selected = false,
                    onClick = { onDelete(droppedItem) })
        )
        Spacer(modifier = Modifier.padding(24.dp))
        Button(
            onClick = { goToVideoScreenshots(droppedItem.id) },
        ) {
            Text(
                text = "Process",
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            )
        }
    }
}
