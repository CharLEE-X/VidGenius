package com.charleex.autoytvid.feature.videoscreenshots

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import src.charleex.autoytvid.processor.screenshot.VideoScreenshotCapturing
import src.charleex.autoytvid.repository.YoutubeRepository
import java.io.File

private typealias VideoScreenshotInputScope = InputHandlerScope<
        VideoScreenshotsContract.Inputs,
        VideoScreenshotsContract.Events,
        VideoScreenshotsContract.State>

internal class VideoScreenshotsInputHandler(
    private val filePath: String,
) :
    KoinComponent,
    InputHandler<VideoScreenshotsContract.Inputs, VideoScreenshotsContract.Events, VideoScreenshotsContract.State> {

    private val repository: YoutubeRepository by inject()
    private val videoScreenshotCapturing: VideoScreenshotCapturing by inject()

    override suspend fun VideoScreenshotInputScope.handleInput(
        input: VideoScreenshotsContract.Inputs,
    ) = when (input) {
        is VideoScreenshotsContract.Inputs.Update -> when (input) {
            is VideoScreenshotsContract.Inputs.Update.Name -> updateState { it.copy(name = input.name) }
            is VideoScreenshotsContract.Inputs.Update.Path -> updateState { it.copy(path = input.path) }
            is VideoScreenshotsContract.Inputs.Update.Duration -> updateState { it.copy(duration = input.duration) }
            is VideoScreenshotsContract.Inputs.Update.Screenshots -> updateState { it.copy(screenshots = input.images) }
            is VideoScreenshotsContract.Inputs.Update.Processing -> updateState { it.copy(processing = input.processing) }
        }

        VideoScreenshotsContract.Inputs.Init -> initVideo()
        VideoScreenshotsContract.Inputs.GetScreenshots -> getScreenshots(videoScreenshotCapturing)
    }

    private fun VideoScreenshotInputScope.initVideo() {
        sideJob("initVideo") {
            val file = File(filePath)
            postInput(VideoScreenshotsContract.Inputs.Update.Path(path = file.name))
            postInput(VideoScreenshotsContract.Inputs.Update.Name(name = filePath))
            videoScreenshotCapturing.getVideoDuration(file)?.let { duration ->
                postInput(VideoScreenshotsContract.Inputs.Update.Duration(duration = duration.toString()))
            }
        }
    }

    private suspend fun VideoScreenshotInputScope.getScreenshots(
        videoScreenshotCapturing: VideoScreenshotCapturing,
    ) {
        PrintlnLogger().debug("Getting screenshots")
        val percentages = getCurrentState().percentages
        val screenshots = getCurrentState().screenshots.toMutableList()
        sideJob("getScreenshots") {
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(processing = true))
            val file = File(filePath)
            try {
                videoScreenshotCapturing.captureScreenshots(file, percentages).collect { screenshot ->
                    screenshots.add(screenshot)
                    postInput(VideoScreenshotsContract.Inputs.Update.Screenshots(images = screenshots))
                }
            } catch (e: Exception) {
                postEvent(VideoScreenshotsContract.Events.ShowError(message = e.message ?: "Error getting screenshots"))
                postInput(VideoScreenshotsContract.Inputs.Update.Processing(processing = false))
                return@sideJob
            }
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(processing = false))
        }
    }
}
