package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.OpenAiRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import kotlinx.coroutines.delay

internal suspend fun ProcessVideoInputScope.handleMetaData(
    input: ProcessVideoContract.Inputs.MetaData,
    openAiRepository: OpenAiRepository,
) {
    when (input) {
        is ProcessVideoContract.Inputs.MetaData.SetState -> updateState { it.copy(metaDataState = input.metaState) }
        ProcessVideoContract.Inputs.MetaData.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isMetaDataOpen
            updateState { it.copy(isMetaDataOpen = isOpen) }
        }

        ProcessVideoContract.Inputs.MetaData.GetMeta -> getMetaData(openAiRepository)
    }
}

private suspend fun ProcessVideoInputScope.getMetaData(openAiRepository: OpenAiRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("uploadVideo") {
        postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.InProgress(0f)))
        try {
//            youtubeRepository.uploadVideo(uiVideo.id, timestamps).collect { progress ->
//                postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.InProgress(progress)))
//            }

            delay(1000)
            postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Success(null)))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting screenshots"
            postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Error(message)))
            return@sideJob
        }
        postInput(ProcessVideoContract.Inputs.Upload.UploadVideo)
    }
}