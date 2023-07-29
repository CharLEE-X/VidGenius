package com.charleex.vidgenius.feature.dragdrop

import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.dragdrop.model.ProgressState
import com.charleex.vidgenius.feature.dragdrop.model.UiVideo
import com.charleex.vidgenius.feature.dragdrop.model.toUiVideo
import com.charleex.vidgenius.feature.dragdrop.model.video
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private typealias ProcessVideoInputScope = InputHandlerScope<
        ProcessVideoContract.Inputs,
        ProcessVideoContract.Events,
        ProcessVideoContract.State>

internal class ProcessVideoInputHandler :
    KoinComponent,
    InputHandler<ProcessVideoContract.Inputs, ProcessVideoContract.Events, ProcessVideoContract.State> {

    private val videoRepository: VideoRepository by inject()

    override suspend fun ProcessVideoInputScope.handleInput(
        input: ProcessVideoContract.Inputs,
    ) = when (input) {
        is ProcessVideoContract.Inputs.Video -> when (input) {
            is ProcessVideoContract.Inputs.Video.ObserveUiVideo -> observeUiVideo()
            is ProcessVideoContract.Inputs.Video.SetState -> updateState { it.copy(uiVideoState = input.uiVideoState) }
            is ProcessVideoContract.Inputs.Video.SetUiVideo -> updateState { it.copy(uiVideo = input.uiVideo) }
        }

        is ProcessVideoContract.Inputs.DragDrop -> when (input) {
            is ProcessVideoContract.Inputs.DragDrop.SetState -> updateState { it.copy(dragDropState = input.dragDropState) }
            is ProcessVideoContract.Inputs.DragDrop.InitVideo -> getFile(input.files)
            is ProcessVideoContract.Inputs.DragDrop.DeleteFile -> deleteFile(input.uiVideo)
        }

        is ProcessVideoContract.Inputs.Screenshots -> when (input) {
            is ProcessVideoContract.Inputs.Screenshots.SetState -> updateState { it.copy(screenshotsState = input.screenshotsState) }
            is ProcessVideoContract.Inputs.Screenshots.GetScreenshots -> TODO()
        }

        is ProcessVideoContract.Inputs.Description -> when (input) {
            is ProcessVideoContract.Inputs.Description.SetState -> updateState { it.copy(descriptionState = input.descriptionState) }
            is ProcessVideoContract.Inputs.Description.GetDescription -> TODO()
        }

        is ProcessVideoContract.Inputs.MeatData -> when (input) {
            is ProcessVideoContract.Inputs.MeatData.SetState -> updateState { it.copy(metaState = input.metaState) }
            is ProcessVideoContract.Inputs.MeatData.GetMeta -> TODO()
        }

        is ProcessVideoContract.Inputs.Upload -> when (input) {
            is ProcessVideoContract.Inputs.Upload.SetState -> updateState { it.copy(uploadState = input.uploadState) }
            is ProcessVideoContract.Inputs.Upload.UploadVideo -> TODO()
        }
    }

    private suspend fun ProcessVideoInputScope.observeUiVideo() {
        sideJob("observeFiles") {
            videoRepository.flowOfVideos().collect { videos ->
                videos.firstOrNull()?.let { video ->
                    val uiVideo = video.toUiVideo()
                    val file = File(uiVideo.path)
                    val name = file.name
                    postInput(ProcessVideoContract.Inputs.Video.SetUiVideo(uiVideo = uiVideo))
                    postInput(ProcessVideoContract.Inputs.DragDrop.SetState(ProgressState.Success(name)))
                } ?: run {
                    postInput(ProcessVideoContract.Inputs.Video.SetUiVideo(uiVideo = null))
                    postInput(ProcessVideoContract.Inputs.DragDrop.SetState(ProgressState.None))
                }
            }
        }
    }

    private fun ProcessVideoInputScope.deleteFile(uiVideo: UiVideo) {
        sideJob("deleteFile") {
            PrintlnLogger().debug("Deleting video ${uiVideo.video().path}")
            videoRepository.deleteVideo(uiVideo.id)
        }
    }

    private suspend fun ProcessVideoInputScope.getFile(files: List<*>) {
        sideJob("getFiles") {
            try {
                videoRepository.filterVideos(files)
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.message ?: "Error while getting file"
                postInput(ProcessVideoContract.Inputs.DragDrop.SetState(ProgressState.Error(message)))
            }
        }
    }
}
