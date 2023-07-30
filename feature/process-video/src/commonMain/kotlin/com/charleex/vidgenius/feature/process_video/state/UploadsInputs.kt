package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import kotlinx.coroutines.delay

internal suspend fun ProcessVideoInputScope.handleUploads(
    input: ProcessVideoContract.Inputs.Upload,
//    youtubeRepository: YoutubeRepository
) {
    when (input) {
        is ProcessVideoContract.Inputs.Upload.SetState -> updateState { it.copy(uploadState = input.uploadState) }
        ProcessVideoContract.Inputs.Upload.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isUploadOpen
            updateState { it.copy(isUploadOpen = isOpen) }
        }

        is ProcessVideoContract.Inputs.Upload.UploadVideo -> uploadVideo(
//            youtubeRepository
        )
    }
}

private suspend fun ProcessVideoInputScope.uploadVideo(
//    youtubeRepository: YoutubeRepository
) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("uploadVideo") {
        postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.InProgress(0f)))
        try {
//            youtubeRepository.uploadVideo(uiVideo.id, timestamps).collect { progress ->
//                postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.InProgress(progress)))
//            }
            delay(1000)
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Error("Error uploading video")))
//            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Success(null)))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting screenshots"
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Error(message)))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
//        postInput(ProcessVideoContract.Inputs.Description.GetDescription)
    }
}
