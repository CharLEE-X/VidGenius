package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.datasource.ScreenshotRepository
import com.charleex.vidgenius.feature.videoscreenshots.model.toUiVideo
import com.charleex.vidgenius.feature.videoscreenshots.model.toVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
            is VideoScreenshotsContract.Inputs.Update.Timestamps -> updateState { it.copy(timestamps = input.timestamps) }
        }

        VideoScreenshotsContract.Inputs.Init -> initVideo()
        VideoScreenshotsContract.Inputs.ObserveVideo -> observeVideo()
        VideoScreenshotsContract.Inputs.GetTimestamps -> getTimeStamps()
        VideoScreenshotsContract.Inputs.CaptureScreenshots -> getScreenshots(screenshotRepository)
        is VideoScreenshotsContract.Inputs.OnScreenshotNotExist -> onScreenshotNotExist(input.screenshotPath)
        is VideoScreenshotsContract.Inputs.DeleteScreenshot -> deleteScreenshot(input.screenshotId)
        is VideoScreenshotsContract.Inputs.SaveTimestamp -> saveTimestamp(input.timestamp)
        is VideoScreenshotsContract.Inputs.DeleteTimestamp -> deleteTimestamp(input.timestamp)
    }

    private suspend fun VideoScreenshotInputScope.initVideo() {
        postInput(VideoScreenshotsContract.Inputs.ObserveVideo)
        postInput(VideoScreenshotsContract.Inputs.GetTimestamps)
    }

    private suspend fun VideoScreenshotInputScope.observeVideo() {
        sideJob("initVideo") {
            screenshotRepository.flowOfVideo(videoId).collect { video ->
                val uiVideo = video.toUiVideo()
                postInput(VideoScreenshotsContract.Inputs.Update.Video(video = uiVideo))
            }
        }
    }

    private suspend fun VideoScreenshotInputScope.getTimeStamps() {
        val uiVideo = getCurrentState().video
        val video = uiVideo.toVideo()
        val duration = try {
            screenshotRepository.getVideoDuration(videoId)
        } catch (e: Exception) {
            postEvent(
                VideoScreenshotsContract.Events.ShowError(
                    message = e.message ?: "Error getting video duration"
                )
            )
            return
        }
        val timestamps = mutableListOf<Long>()
        val numberOfTimestamps = VideoScreenshotsViewModel.DEFAULT_NUMBER_OF_TIMESTAMPS
        val chunks = numberOfTimestamps + 2
        val interval = duration / chunks
        for (i in 1..chunks) {
            if (i == 1 || i == chunks) {
                continue
            }
            val timestamp = interval * i
            logger.debug("Adding Timestamp: $timestamp, Chunk: $i, Interval: $interval")
            timestamps.add(timestamp)
        }
        logger.debug("Initial Timestamps: $timestamps")
        postInput(VideoScreenshotsContract.Inputs.Update.Timestamps(timestamps = timestamps))
    }

    private suspend fun VideoScreenshotInputScope.onScreenshotNotExist(screenshotPath: String) {
        val video = getCurrentState().video
        val uiScreenshot = video.screenshots.find { it.path == screenshotPath }
        uiScreenshot?.let { screenshot ->
            postInput(VideoScreenshotsContract.Inputs.DeleteScreenshot(screenshotId = screenshot.id))
        } ?: run {
            postEvent(VideoScreenshotsContract.Events.ShowError(message = "Screenshot not found"))
        }
    }

    private suspend fun VideoScreenshotInputScope.deleteScreenshot(screenshotId: String) {
        val video = getCurrentState().video
        screenshotRepository.deleteScreenshot(video.id, screenshotId)
    }

    private suspend fun VideoScreenshotInputScope.saveTimestamp(timestamp: Long) {
        val timestamps = getCurrentState().timestamps
        val updatedTimestamps = timestamps + timestamp
        postInput(VideoScreenshotsContract.Inputs.Update.Timestamps(timestamps = updatedTimestamps))
    }

    private suspend fun VideoScreenshotInputScope.deleteTimestamp(timestamp: Long) {
        val timestamps = getCurrentState().timestamps
        val updatedTimestamps = timestamps - timestamp
        postInput(VideoScreenshotsContract.Inputs.Update.Timestamps(timestamps = updatedTimestamps))
    }

    private suspend fun VideoScreenshotInputScope.getScreenshots(
        screenshotRepository: ScreenshotRepository,
    ) {
        PrintlnLogger().debug("Getting screenshots")
        val timestamps = getCurrentState().timestamps
        val uiVideo = getCurrentState().video
        sideJob("getScreenshots") {
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = true))
            uiVideo.screenshots.forEach {
                postInput(VideoScreenshotsContract.Inputs.DeleteScreenshot(screenshotId = it.id))
            }
            try {
                screenshotRepository.captureScreenshots(uiVideo.id, timestamps)
            } catch (e: Exception) {
                postEvent(
                    VideoScreenshotsContract.Events.ShowError(
                        message = e.message ?: "Error getting screenshots"
                    )
                )
                postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = false))
                return@sideJob
            }
            postInput(VideoScreenshotsContract.Inputs.Update.Processing(isProcessing = false))
        }
    }
}
