package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent


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
                println("Dropped: ${droppedFiles.size}")
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
            Text(
                text = "Drag and drop a file or directory here to add videos",
                color = MaterialTheme.colors.onSurface,
            )
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
