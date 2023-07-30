package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.charleex.vidgenius.feature.process_video.model.UiVideo
import com.charleex.vidgenius.feature.process_video.model.toUiVideo
import com.charleex.vidgenius.feature.process_video.model.video
import com.copperleaf.ballast.core.PrintlnLogger
import java.io.File

internal suspend fun ProcessVideoInputScope.handleVideo(
    input: ProcessVideoContract.Inputs.Video,
    videoRepository: VideoRepository
) = when (input) {
    is ProcessVideoContract.Inputs.Video.ObserveUiVideo -> observeUiVideo(videoRepository)
    is ProcessVideoContract.Inputs.Video.SetState -> updateState { it.copy(uiVideoState = input.uiVideoState) }
    is ProcessVideoContract.Inputs.Video.SetUiVideo -> updateState { it.copy(uiVideo = input.uiVideo) }
    is ProcessVideoContract.Inputs.Video.ProcessVideo -> processVideo()
}

internal suspend fun ProcessVideoInputScope.handleDragDrop(
    input: ProcessVideoContract.Inputs.DragDrop,
    videoRepository: VideoRepository
) = when (input) {
    is ProcessVideoContract.Inputs.DragDrop.SetState -> updateState { it.copy(dragDropState = input.dragDropState) }
    ProcessVideoContract.Inputs.DragDrop.ToggleIsOpen -> {
        val isOpen = !getCurrentState().isDragDropOen
        updateState { it.copy(isDragDropOen = isOpen) }
    }

    is ProcessVideoContract.Inputs.DragDrop.InitVideo -> getFile(input.files, videoRepository)
    is ProcessVideoContract.Inputs.DragDrop.DeleteFile -> deleteFile(input.uiVideo, videoRepository)
}

private suspend fun ProcessVideoInputScope.observeUiVideo(videoRepository: VideoRepository) {
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

private fun ProcessVideoInputScope.deleteFile(uiVideo: UiVideo, videoRepository: VideoRepository) {
    sideJob("deleteFile") {
        PrintlnLogger().debug("Deleting video ${uiVideo.video().path}")
        videoRepository.deleteVideo(uiVideo.id)
        postInput(ProcessVideoContract.Inputs.Video.SetState(ProgressState.None))
        postInput(ProcessVideoContract.Inputs.DragDrop.SetState(ProgressState.None))
        postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.None))
        postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.None))
        postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.None))
        postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.None))
    }
}

private suspend fun ProcessVideoInputScope.getFile(files: List<*>, videoRepository: VideoRepository) {
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

private suspend fun ProcessVideoInputScope.processVideo() {
    val screenshotsQuantity = getCurrentState().screenshotsQuantity
    sideJob("processVideo") {
        postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Queued))
        postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.Queued))
        postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Queued))
        postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Queued))
        postInput(ProcessVideoContract.Inputs.DragDrop.ToggleIsOpen)

        postInput(ProcessVideoContract.Inputs.Screenshots.ProcessScreenshots(quantity = screenshotsQuantity))
    }
}
