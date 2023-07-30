package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.YoutubeRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState

internal suspend fun ProcessVideoInputScope.handleUploads(
    input: ProcessVideoContract.Inputs.Upload,
    youtubeRepository: YoutubeRepository
) {
    when (input) {
        is ProcessVideoContract.Inputs.Upload.SetState -> updateState { it.copy(uploadState = input.uploadState) }
        ProcessVideoContract.Inputs.Upload.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isUploadOpen
            updateState { it.copy(isUploadOpen = isOpen) }
        }

        is ProcessVideoContract.Inputs.Upload.UploadVideo -> uploadVideo(youtubeRepository)
        is ProcessVideoContract.Inputs.Upload.GetYoutubeVideoLink -> getYoutubeVideoLink(youtubeRepository)
    }
}

private suspend fun ProcessVideoInputScope.uploadVideo(youtubeRepository: YoutubeRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("uploadVideo") {
        postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.InProgress(0f)))
        try {
            youtubeRepository.uploadVideo(uiVideo.id).collect { progress ->
                postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.InProgress(progress)))
            }
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Success(null)))
            postInput(ProcessVideoContract.Inputs.Upload.GetYoutubeVideoLink)
        } catch (e: Exception) {
            val message = e.message ?: "Error getting screenshots"
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Error(message)))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
    }
}

private suspend fun ProcessVideoInputScope.getYoutubeVideoLink(youtubeRepository: YoutubeRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("getYoutubeVideoLink") {
        try {
            val link = youtubeRepository.getYoutubeVideoLink(uiVideo.id)
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Success("Youtube: $link")))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting Youtube link"
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Error(message)))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
    }
}
