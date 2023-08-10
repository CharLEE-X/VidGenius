package com.charleex.vidgenius.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent

@Composable
internal fun DropTarget(
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

}
