package com.charleex.autoytvid.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VideoFile
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.charleex.autoytvid.feature.videodetail.DragDropContract
import com.charleex.autoytvid.feature.videodetail.DragDropItem
import com.charleex.autoytvid.feature.videodetail.DragDropViewModel
import com.charleex.autoytvid.ui.components.AppCard
import com.charleex.autoytvid.ui.components.AppFlexSpacer
import com.charleex.autoytvid.ui.util.Breakpoint
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import kotlin.time.ExperimentalTime

@Composable
internal fun DragDropContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    window: ComposeWindow,
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
                onDropped = {
                    vm.trySend(DragDropContract.Inputs.GetFiles(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
            DroppedFiles(
                files = state.videos,
                onDelete = {
                    vm.trySend(DragDropContract.Inputs.DeleteFile(it))
                }
            )
        }
    }
}

@Composable
internal fun DragArea(
    modifier: Modifier,
    window: ComposeWindow,
    onDropped: (List<*>) -> Unit,
) {
    val target = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>
                onDropped(droppedFiles)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    window.contentPane.dropTarget = target

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.primary
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = "",
                onValueChange = { },
                singleLine = true,
                enabled = false,
                readOnly = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Search
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Transparent,
                    cursorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .selectable(
                        selected = false,
                        onClick = { })
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = { }
                    )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Drag and drop a file or directory here to add videos")
            Icon(
                imageVector = Icons.Default.VideoFile,
                contentDescription = "Drag and drop",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun DroppedFiles(
    files: List<DragDropItem>,
    onDelete: (DragDropItem) -> Unit,
) {
    files.forEach {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }
        AnimatedVisibility(visible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 24.dp
                    ),
            ) {
                Text(
                    text = it.videoType.name,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(it.path)
                AppFlexSpacer()
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(8.dp)
                        .selectable(
                            selected = false,
                            onClick = { onDelete(it) })
                )
            }
        }
    }
}
