package com.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger
import java.io.File

interface LocalVideoProcessor {
    suspend fun filterVideos(files: List<*>): List<File>

    suspend fun captureScreenshots(
        videoPath: String,
        numberOfScreenshots: Int,
    ): List<String>
}

internal class LocalVideoProcessorImpl(
    private val logger: Logger,
    private val fileProcessor: FileProcessor,
    private val screenshotCapturing: ScreenshotCapturing,
) : LocalVideoProcessor {
    override suspend fun filterVideos(files: List<*>): List<File> {
        logger.d("Filtering videos from files")
        return fileProcessor.filterVideoFiles(files)
    }

    override suspend fun captureScreenshots(
        videoPath: String,
        numberOfScreenshots: Int,
    ): List<String> {
        logger.d("Getting screenshots for videoId $videoPath")
        val file = File(videoPath)
        if (!file.exists()) error("File does not exist: $videoPath")
        val videoDuration = getVideoDuration(videoPath)
        val timestamps = getTimestamps(numberOfScreenshots, videoDuration)

        return timestamps.mapIndexed { index: Int, timestamp: Long ->
            val screenshotFile = screenshotCapturing.captureScreenshot(file, timestamp, index)
            screenshotFile.absolutePath
        }
    }

    private fun getVideoDuration(localVideoPath: String): Long {
        val file = File(localVideoPath)
        return screenshotCapturing.getVideoDuration(file)
    }

    private fun getTimestamps(
        quantity: Int,
        duration: Long,
    ): List<Long> {
        val timestamps = mutableListOf<Long>()
        val chunks = quantity + 2
        val interval = duration / chunks
        for (i in 1..chunks) {
            if (i == 1 || i == chunks) {
                continue
            }
            val timestamp = interval * i
            logger.d("Adding Timestamp: $timestamp, Chunk: $i, Interval: $interval")
            timestamps.add(timestamp)
        }
        return timestamps
    }
}
