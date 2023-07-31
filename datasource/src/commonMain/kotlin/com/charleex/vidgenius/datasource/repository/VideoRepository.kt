package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.Screenshot
import com.hackathon.cda.repository.db.VidGeniusDatabase
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import src.charleex.vidgenius.processor.file.FileProcessor
import src.charleex.vidgenius.processor.screenshot.ScreenshotCapturing
import java.io.File

interface VideoRepository {
    fun getVideoById(videoId: String): Video
    fun flowOfVideos(): Flow<List<Video>>
    fun deleteVideo(videoId: String)

    suspend fun captureScreenshots(
        videoId: String,
        numberOfScreenshots: Int,
        onProgress: suspend (Float) -> Unit,
    )

    suspend fun filterVideos(files: List<*>)
}

internal class VideoRepositoryImpl(
    private val logger: Logger,
    private val fileProcessor: FileProcessor,
    private val screenshotCapturing: ScreenshotCapturing,
    private val database: VidGeniusDatabase,
) : VideoRepository {
    override fun getVideoById(videoId: String): Video {
        return database.videoQueries.getById(videoId).executeAsOne()
    }

    override fun flowOfVideos(): Flow<List<Video>> {
        logger.d("Getting flow of all videos")
        return database.videoQueries.getAll().asFlow()
            .map { it.executeAsList() }
            .onEach { logger.d("Videos: $it") }
    }

    override fun deleteVideo(videoId: String) {
        val video = getVideoById(videoId)
        logger.d("Deleting video ${video.path}")
        if (video.screenshots.isNotEmpty()) {
            logger.d("Video ${video.path} has screenshots")
            video.screenshots.forEach { screenshot ->
                deleteScreenshot(video.id, screenshot.id)
            }
        } else {
            logger.d("Video ${video.path} has no screenshots")
        }
        database.videoQueries.delete(video.id)
    }

    override suspend fun captureScreenshots(
        videoId: String,
        numberOfScreenshots: Int,
        onProgress: suspend (Float) -> Unit,
    ) {
        logger.d("Getting screenshots for videoId $videoId")
        val video = getVideoById(videoId)
        val file = File(video.path)
        val videoDuration = getVideoDuration(videoId)
        val timestamps = getTimestamps(numberOfScreenshots, videoDuration)

        timestamps.forEachIndexed { index: Int, timestamp: Long ->
            val screenshotFile = screenshotCapturing.captureScreenshot(file, timestamp, index)
            val screenshot = Screenshot(
                path = screenshotFile.absolutePath,
            )

            val currentVideo = getVideoById(videoId)
            val screenshots = currentVideo.screenshots + screenshot
            val videoToUpdate = video.copy(
                screenshots = screenshots,
                modifiedAt = Clock.System.now(),
            )
            database.videoQueries.upsert(videoToUpdate)
            onProgress(index.toFloat() / timestamps.size)
            logger.d("Screenshot ${screenshotFile.absolutePath} saved")
        }
    }

    override suspend fun filterVideos(files: List<*>) {
        logger.d("Getting videos from files")
        val videos = fileProcessor.filterVideoFiles(files)
        val localVideos = database.videoQueries.getAll().executeAsList()
        val filteredVideos = videos.filter {
            it.path !in localVideos.map { video -> video.path }
        }
        filteredVideos.forEach { video ->
            logger.d("Storing video ${video.absolutePath}")
            database.videoQueries.upsert(
                Video(
                    id = uuid4().toString(),
                    path = video.absolutePath,
                    screenshots = emptyList(),
                    descriptions = emptyList(),
                    descriptionContext = null,
                    title = null,
                    description = null,
                    tags = emptyList(),
                    youtubeVideoId = null,
                    createdAt = Clock.System.now(),
                    modifiedAt = Clock.System.now(),
                )
            )
        }
    }

    private fun deleteScreenshot(videoId: String, screenshotPath: String) {
        val video = getVideoById(videoId)
        val screenshot = video.screenshots.find { it.path == screenshotPath }
        if (screenshot == null) {
            logger.d("Screenshot $screenshotPath not found")
            return
        }
        logger.d("Deleting screenshot ${screenshot.id}")
        val screenshots = video.screenshots.ifEmpty { return }
        val updatedScreenshots = screenshots - screenshot
        val updatedVideo = video.copy(
            screenshots = updatedScreenshots,
            modifiedAt = Clock.System.now()
        )
        database.videoQueries.upsert(updatedVideo)
        fileProcessor.deleteFile(screenshot.path)
    }

    private fun getVideoDuration(videoId: String): Long {
        val video = getVideoById(videoId)
        val file = File(video.path)
        if (!file.exists()) error("File does not exist")
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
