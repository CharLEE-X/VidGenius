package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.GoogleCloudRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState

internal suspend fun ProcessVideoInputScope.handleDescriptions(
    input: ProcessVideoContract.Inputs.Description,
    googleCloudRepository: GoogleCloudRepository,
) {
    when (input) {
        is ProcessVideoContract.Inputs.Description.SetState -> updateState { it.copy(descriptionState = input.descriptionState) }
        ProcessVideoContract.Inputs.Description.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isDescriptionOpen
            updateState { it.copy(isDescriptionOpen = isOpen) }
        }

        ProcessVideoContract.Inputs.Description.GetDescription -> processDescription(googleCloudRepository)
    }
}

private suspend fun ProcessVideoInputScope.processDescription(googleCloudRepository: GoogleCloudRepository) {
    val video = getCurrentState().uiVideo ?: return
    sideJob("getTextFromScreenshots") {
        try {
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.InProgress(0f)))
            googleCloudRepository.getTextFromImage(video.id).collect { progress ->
                postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.InProgress(progress)))
            }
            postInput(ProcessVideoContract.Inputs.Description.SetState(ProgressState.Success(null)))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting text from images"
            postEvent(ProcessVideoContract.Events.ShowError(message))
            return@sideJob
        }
        postInput(ProcessVideoContract.Inputs.MetaData.GetMeta)
    }
}
