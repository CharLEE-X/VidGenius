package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.datasource.GoogleCloudRepository
import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.videoscreenshots.model.toUiVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private typealias VideoScreenshotInputScope = InputHandlerScope<
        ScreenshotsToTextContract.Inputs,
        ScreenshotsToTextContract.Events,
        ScreenshotsToTextContract.State>

internal class ScreenshotsToTextInputHandler(
    private val videoId: String,
) : KoinComponent,
    InputHandler<ScreenshotsToTextContract.Inputs, ScreenshotsToTextContract.Events, ScreenshotsToTextContract.State> {

    private val videoRepository: VideoRepository by inject()
    private val googleCloudRepository: GoogleCloudRepository by inject()

    override suspend fun VideoScreenshotInputScope.handleInput(
        input: ScreenshotsToTextContract.Inputs,
    ) = when (input) {
        is ScreenshotsToTextContract.Inputs.Update -> when (input) {
            is ScreenshotsToTextContract.Inputs.Update.Video -> updateState { it.copy(video = input.video) }
            is ScreenshotsToTextContract.Inputs.Update.Processing -> updateState {
                it.copy(processing = input.isProcessing)
            }

            is ScreenshotsToTextContract.Inputs.Update.Progress -> updateState {
                it.copy(progress = input.progress)
            }
        }

        ScreenshotsToTextContract.Inputs.Init -> initVideo()
        ScreenshotsToTextContract.Inputs.ObserveVideo -> observeVideo()
        ScreenshotsToTextContract.Inputs.GetTextFromScreenshots ->
            getTextFromScreenshots()
    }

    private suspend fun VideoScreenshotInputScope.initVideo() {
        postInput(ScreenshotsToTextContract.Inputs.Update.Processing(isProcessing = true))
        postInput(ScreenshotsToTextContract.Inputs.ObserveVideo)
        postInput(ScreenshotsToTextContract.Inputs.GetTextFromScreenshots)
        postInput(ScreenshotsToTextContract.Inputs.Update.Processing(isProcessing = false))
    }

    private suspend fun VideoScreenshotInputScope.observeVideo() {
        PrintlnLogger().debug("Observing video")
        sideJob("initVideo") {
            videoRepository.flowOfVideo(videoId).collect { video ->
                val uiVideo = video.toUiVideo()
                postInput(ScreenshotsToTextContract.Inputs.Update.Video(video = uiVideo))
            }
        }
    }

    private suspend fun VideoScreenshotInputScope.getTextFromScreenshots() {
        PrintlnLogger().debug("Getting text from screenshots")
        sideJob("getTextFromScreenshots") {
//            try {
            googleCloudRepository.getTextFromImage(videoId).collect { progress ->
                postInput(ScreenshotsToTextContract.Inputs.Update.Progress(progress))
            }
//            } catch (e: Exception) {
//                postEvent(
//                    ScreenshotsToTextContract.Events.ShowError(
//                        message = e.message ?: "Error getting text from images"
//                    )
//                )
//                return@sideJob
//            }
        }
    }
}
