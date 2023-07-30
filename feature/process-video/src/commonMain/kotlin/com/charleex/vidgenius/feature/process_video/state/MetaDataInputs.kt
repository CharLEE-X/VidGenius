package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.OpenAiRepository
import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.copperleaf.ballast.postInput
import kotlinx.coroutines.flow.first

internal suspend fun ProcessVideoInputScope.handleMetaData(
    input: ProcessVideoContract.Inputs.MetaData,
    openAiRepository: OpenAiRepository,
    videoRepository: VideoRepository,
) {
    when (input) {
        is ProcessVideoContract.Inputs.MetaData.SetState -> updateState { it.copy(metaDataState = input.metaState) }
        ProcessVideoContract.Inputs.MetaData.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isMetaDataOpen
            updateState { it.copy(isMetaDataOpen = isOpen) }
        }

        ProcessVideoContract.Inputs.MetaData.GetMeta -> getMetaData(openAiRepository)
        ProcessVideoContract.Inputs.MetaData.SetTitle -> setTitle(videoRepository)
    }
}

private suspend fun ProcessVideoInputScope.setTitle(videoRepository: VideoRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    val video = videoRepository.flowOfVideo(uiVideo.id).first()
    postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Success("${ video.title } Success")))
}

private suspend fun ProcessVideoInputScope.getMetaData(openAiRepository: OpenAiRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("uploadVideo") {
        postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.InProgress(0f)))
        try {
            openAiRepository.getMetaData(uiVideo.id).collect { progress ->
                postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.InProgress(progress)))
            }
            postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Success(null)))
            postInput(ProcessVideoContract.Inputs.MetaData.SetTitle)
        } catch (e: Exception) {
            val message = e.message ?: "Error getting meta-data"
            postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Error(message)))
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Cancelled))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
        postInput(ProcessVideoContract.Inputs.Upload.UploadVideo)
    }
}
