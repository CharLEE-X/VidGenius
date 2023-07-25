package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.feature.videoscreenshots.model.toUiVideo
import com.charleex.vidgenius.feature.videoscreenshots.model.toVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.charleex.vidgenius.datasource.ScreenshotRepository

private typealias VideoScreenshotInputScope = InputHandlerScope<
        VideoScreenshotsContract.Inputs,
        VideoScreenshotsContract.Events,
        VideoScreenshotsContract.State>

internal class VideoScreenshotsInputHandler(
    private val videoId: String,
) : KoinComponent,
    InputHandler<VideoScreenshotsContract.Inputs, VideoScreenshotsContract.Events, VideoScreenshotsContract.State> {

    private val screenshotRepository: ScreenshotRepository by inject()

    override suspend fun VideoScreenshotInputScope.handleInput(
        input: VideoScreenshotsContract.Inputs,
    ) = when (input) {
        is VideoScreenshotsContract.Inputs.Update -> when (input) {
            is VideoScreenshotsContract.Inputs.Update.Video -> updateState { it.copy(video = input.video) }
            is VideoScreenshotsContract.Inputs.Update.Processing -> updateState { it.copy(processing = input.isProcessing) }
            is VideoScreenshotsContract.Inputs.Update.Percentages -> updateState { it.copy(percentages = input.percentages) }
        }

        VideoScreenshotsContract.Inputs.Init -> initVideo()
        VideoScreenshotsContract.Inputs.CaptureScreenshots -> getScreenshots(screenshotRepository)
    }

    private fun VideoScreenshotInputScope.initVideo() {
        sideJob("initVideo") {
            screenshotRepository.flowOfVideo(videoId).collect { video ->
                val uiVideo = video.toUiVideo()
                postInput(VideoScreenshotsContract.Inputs.Update.Video(video = uiVideo))
            }
        }
    }

    private suspend fun VideoScreenshotInputScope.getScreenshots(
        screenshotRepository: ScreenshotRepository,
    ) {
        PrintlnLogger().debug("Getting screenshots")
        val percentages = getCurrentState().percentages
        val uiVideo = getCurrentState().video
        val video = uiVideo.toVideo()
        sideJob("getScreenshots") {
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = true))
            try {
                screenshotRepository.captureScreenshots(video, percentages)
            } catch (e: Exception) {
                postEvent(VideoScreenshotsContract.Events.ShowError(message = e.message ?: "Error getting screenshots"))
                postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = false))
                return@sideJob
            }
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = false))
        }
    }
}
