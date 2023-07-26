package com.charleex.vidgenius.feature.dragdrop

import com.charleex.vidgenius.datasource.ScreenshotRepository
import com.charleex.vidgenius.feature.dragdrop.model.DragDropItem
import com.charleex.vidgenius.feature.dragdrop.model.toDragDropItem
import com.charleex.vidgenius.feature.dragdrop.model.video
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private typealias DragDropInputScope = InputHandlerScope<
        DragDropContract.Inputs,
        DragDropContract.Events,
        DragDropContract.State>

internal class DragDropInputHandler :
    KoinComponent,
    InputHandler<DragDropContract.Inputs, DragDropContract.Events, DragDropContract.State> {

    private val screenshotRepository: ScreenshotRepository by inject()

    override suspend fun DragDropInputScope.handleInput(
        input: DragDropContract.Inputs,
    ) = when (input) {
        is DragDropContract.Inputs.Update -> when (input) {
            is DragDropContract.Inputs.Update.SetFiles -> updateState { it.copy(dragDropItems = input.dragDropItems) }
            is DragDropContract.Inputs.Update.SetLoading -> updateState { it.copy(loading = input.loading) }
        }

        is DragDropContract.Inputs.ObserveFiles -> observeFiles()
        is DragDropContract.Inputs.GetFiles -> getFiles(input.anyList as List<File>)
        is DragDropContract.Inputs.DeleteFile -> deleteFile(input.dragDropItem)
    }

    private suspend fun DragDropInputScope.observeFiles() {
        PrintlnLogger().debug("Observing files")
        sideJob("observeFiles") {
            screenshotRepository.flowOfVideos().collect { videos ->
                val dragDropItems = videos.map { it.toDragDropItem() }
                postInput(DragDropContract.Inputs.Update.SetFiles(dragDropItems))
            }
        }
    }

    private suspend fun DragDropInputScope.deleteFile(dragDropItem: DragDropItem) {
        sideJob("deleteFile") {
            PrintlnLogger().debug("Deleting video ${dragDropItem.video().path}")
            screenshotRepository.deleteVideo(dragDropItem.id)
        }
    }

    private suspend fun DragDropInputScope.getFiles(files: List<File>) {
        PrintlnLogger().debug("Getting files")
        sideJob("getFiles") {
            try {
                screenshotRepository.filterVideos(files)
            } catch (e: Exception) {
                PrintlnLogger().error(e)
                postEvent(DragDropContract.Events.ShowError(e.message ?: "Error while getting files"))
            }
        }
    }
}
