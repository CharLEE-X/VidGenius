package com.charleex.vidgenius.feature.process_video.state

import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoInputScope
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.copperleaf.ballast.postInput

internal suspend fun ProcessVideoInputScope.handleScreenshots(
    input: ProcessVideoContract.Inputs.Screenshots,
    videoRepository: VideoRepository
) {
    when (input) {
        is ProcessVideoContract.Inputs.Screenshots.SetState -> updateState { it.copy(screenshotsState = input.screenshotsState) }
        ProcessVideoContract.Inputs.Screenshots.ToggleIsOpen -> {
            val isOpen = !getCurrentState().isScreenshotsOpen
            updateState { it.copy(isScreenshotsOpen = isOpen) }
        }

        is ProcessVideoContract.Inputs.Screenshots.ProcessScreenshots ->
            processScreenshots(input.quantity, videoRepository)

        is ProcessVideoContract.Inputs.Screenshots.DeleteScreenshot ->
            deleteScreenshot(input.screenshotPath, videoRepository)
    }
}

private suspend fun ProcessVideoInputScope.processScreenshots(
    quantity: Int,
    videoRepository: VideoRepository,
) {
    val uiVideo = getCurrentState().uiVideo ?: return
    val timestamps = getTimestamps(uiVideo.id, quantity, videoRepository)
    if (timestamps.isEmpty()) {
        val message = "No timestamps found"
        postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Error(message)))
        return
    }
    sideJob("getScreenshots") {
        postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.InProgress(0f)))
        try {
            videoRepository.captureScreenshots(uiVideo.id, timestamps).collect { progress ->
                postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.InProgress(progress)))
            }
            postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Success(null)))
        } catch (e: Exception) {
            val message = e.message ?: "Error getting screenshots"
            postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Error(message)))
            return@sideJob
        }
        postInput(ProcessVideoContract.Inputs.Description.GetDescription)
    }
}

private suspend fun ProcessVideoInputScope.deleteScreenshot(screenshotPath: String, videoRepository: VideoRepository) {
    val video = getCurrentState().uiVideo ?: return
    videoRepository.deleteScreenshot(video.id, screenshotPath)
}

private suspend fun ProcessVideoInputScope.getTimestamps(
    videoId: String,
    quantity: Int,
    videoRepository: VideoRepository,
): List<Long> {
    val duration = getDuration(videoId, videoRepository) ?: return emptyList()
    val timestamps = mutableListOf<Long>()
    val chunks = quantity + 2
    val interval = duration / chunks
    for (i in 1..chunks) {
        if (i == 1 || i == chunks) {
            continue
        }
        val timestamp = interval * i
        logger.debug("Adding Timestamp: $timestamp, Chunk: $i, Interval: $interval")
        timestamps.add(timestamp)
    }
    return timestamps
}

private suspend fun ProcessVideoInputScope.getDuration(
    videoId: String,
    videoRepository: VideoRepository,
): Long? {
    return try {
        videoRepository.getVideoDuration(videoId)
    } catch (e: Exception) {
        val message = e.message ?: "Error getting video duration"
        postInput(ProcessVideoContract.Inputs.Screenshots.SetState(ProgressState.Error(message)))
        return null
    }
}
