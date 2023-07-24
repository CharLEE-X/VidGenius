package com.charleex.autoytvid.feature.videodetail

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import src.charleex.autoytvid.repository.YoutubeRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private typealias DragDropInputScope = InputHandlerScope<
        DragDropContract.Inputs,
        DragDropContract.Events,
        DragDropContract.State>

internal class DragDropInputHandler :
    KoinComponent,
    InputHandler<DragDropContract.Inputs, DragDropContract.Events, DragDropContract.State> {

    private val repository: YoutubeRepository by inject()

    override suspend fun DragDropInputScope.handleInput(
        input: DragDropContract.Inputs,
    ) = when (input) {
        is DragDropContract.Inputs.GetFiles -> getFiles(input.anyList)
        is DragDropContract.Inputs.Update -> when (input) {
            is DragDropContract.Inputs.Update.SetFiles -> updateState { it.copy(dragDropItems = input.dragDropItems) }
            is DragDropContract.Inputs.Update.SetLoading -> updateState { it.copy(loading = input.loading) }
        }

        is DragDropContract.Inputs.DeleteFile -> deleteFile(input.dragDropItem)
    }
}

private suspend fun DragDropInputScope.deleteFile(dragDropItem: DragDropItem) {
    val dragDropItems = getCurrentState().dragDropItems
    val newDragDropItems = dragDropItems - dragDropItem
    postInput(DragDropContract.Inputs.Update.SetFiles(newDragDropItems))
}

private suspend fun DragDropInputScope.getFiles(anyList: List<*>) {
    try {
        val currentDragDropItems = getCurrentState().dragDropItems
        val files = processFileSystemItems(anyList)
        val dragDropItems = files.map { it.toDragDropItem() }
        val allDragDropItems = currentDragDropItems + dragDropItems
        postInput(DragDropContract.Inputs.Update.SetFiles(allDragDropItems))
    } catch (e: Exception) {
        postEvent(
            DragDropContract.Events.ShowError(
                e.message ?: "Error while getting files"
            )
        )
    }
}

private fun processFileSystemItems(items: List<*>): List<File> {
    val files = mutableListOf<File>()

    items.forEach { item ->
        try {
            val file = item as File
            if (file.isFile) {
                PrintlnLogger().debug("item is type file.")
                if (isVideoFile(file.absolutePath)) {
                    PrintlnLogger().debug("item is video file.")
                    if (file.canRead()) {
                        PrintlnLogger().debug("item is readable.")
                        files.add(file)
                    } else {
                        if (file.setReadable(true)) {
                            PrintlnLogger().debug("item is now readable.")
                            files.add(file)
                        } else {
                            PrintlnLogger().debug("item is not readable.")
                        }
                    }
                } else {
                    PrintlnLogger().debug("item is not video file.")
                }
            } else if (file.isDirectory) {
                PrintlnLogger().debug("item is directory.")
                val filesInDirectory = file.listFiles()
                if (filesInDirectory != null) {
                    PrintlnLogger().debug("files in directory: ${filesInDirectory.map { it.absolutePath }}")
                    processFileSystemItems(filesInDirectory.toList())
                } else {
                    PrintlnLogger().debug("files in directory is null.")
                }
            } else {
                PrintlnLogger().debug("item is not type file or directory.")
            }
        } catch (e: Exception) {
            PrintlnLogger().debug("Error while processing file system item: ${e.message}")
        }
    }
    PrintlnLogger().debug("Files: ${files.map { it.absolutePath }}")
    return files
}

private fun isVideoFile(filePath: String): Boolean {
    val contentType = Files.probeContentType(Paths.get(filePath))
    return contentType?.startsWith("video/") ?: false
}

private fun File.toDragDropItem(): DragDropItem {
    return DragDropItem(
        file = this,
        videoType = this.videoType(),
    )
}

private fun File.videoType(): VideoType {
    return when (extension) {
        "mp4" -> VideoType.MP4
        "mkv" -> VideoType.MKV
        "avi" -> VideoType.AVI
        else -> VideoType.UNKNOWN
    }
}
