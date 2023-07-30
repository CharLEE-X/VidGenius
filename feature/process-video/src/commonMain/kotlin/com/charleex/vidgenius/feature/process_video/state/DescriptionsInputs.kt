package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.GoogleCloudRepository
import com.charleex.vidgenius.datasource.OpenAiRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.copperleaf.ballast.core.PrintlnLogger

internal suspend fun ProcessVideoInputScope.handleDescriptions(
    input: ProcessVideoContract.Inputs.Description,
    googleCloudRepository: GoogleCloudRepository,
    openAiRepository: OpenAiRepository,
) {
    when (input) {
        is ProcessVideoContract.Inputs.Description.SetState -> updateState { it.copy(descriptionState = input.descriptionState) }
        ProcessVideoContract.Inputs.Description.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isDescriptionOpen
            updateState { it.copy(isDescriptionOpen = isOpen) }
        }

        ProcessVideoContract.Inputs.Description.GetDescription -> processDescription(googleCloudRepository)
        ProcessVideoContract.Inputs.Description.GetScreenshotContext -> getDescriptionContext(openAiRepository)
    }
}

private suspend fun ProcessVideoInputScope.processDescription(
    googleCloudRepository: GoogleCloudRepository,
) {
    val video = getCurrentState().uiVideo ?: return
    sideJob("getTextFromScreenshots") {
        try {
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.InProgress(0f)))
            googleCloudRepository.getTextFromImage(video.id).collect { progress ->
                postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.InProgress(progress)))
            }
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.Success(null)))
            postInput(ProcessVideoContract.Inputs.Description.GetScreenshotContext)
        } catch (e: Exception) {
            val message = e.message ?: "Error getting text from images"
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.Error(message)))
            postInput(ProcessVideoContract.Inputs.MetaData.SetState(ProgressState.Cancelled))
            postInput(ProcessVideoContract.Inputs.Upload.SetState(ProgressState.Cancelled))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
        postInput(ProcessVideoContract.Inputs.MetaData.GetMeta)
    }
}

private suspend fun ProcessVideoInputScope.getDescriptionContext(openAiRepository: OpenAiRepository) {
    val uiVideo = getCurrentState().uiVideo ?: return
    sideJob("getDescriptionContext") {
        try {
            val descriptionContext = openAiRepository.getDescriptionContext(uiVideo.id)
            PrintlnLogger().info("descriptionContext: $descriptionContext")
            postInput(ProcessVideoContract.Inputs.Video.SetUiVideo(uiVideo.copy(descriptionContext = descriptionContext)))
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.Success(descriptionContext)))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting description context"
            postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Error(message)))
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
    }
}
