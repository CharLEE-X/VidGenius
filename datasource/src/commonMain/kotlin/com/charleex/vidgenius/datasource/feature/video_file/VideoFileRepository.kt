package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.open_ai.model.ContentInfo
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File

interface VideoFileRepository {
    fun getVideoById(videoId: String): Video
    fun flowOfVideos(): Flow<List<Video>>
    fun deleteVideo(videoId: String)

    suspend fun captureScreenshots(
        video: Video,
        numberOfScreenshots: Int,
    ): Video

    suspend fun filterVideos(files: List<*>)
}

internal class VideoFileRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val fileProcessor: FileProcessor,
    private val screenshotCapturing: ScreenshotCapturing,
) : VideoFileRepository {
    override fun getVideoById(videoId: String): Video {
        return database.videoQueries.getById(videoId).executeAsOne()
    }

    override fun flowOfVideos(): Flow<List<Video>> {
        logger.d("Getting flow of all videos")
        return database.videoQueries.getAll().asFlow()
            .map { it.executeAsList() }
//            .onEach { logger.d("Videos: $it") }
    }

    override fun deleteVideo(videoId: String) {
        val video = getVideoById(videoId)
        logger.d("Deleting video ${video.path}")
        if (video.screenshots.isNotEmpty()) {
            logger.d("Video ${video.path} has screenshots")
            video.screenshots.forEach { screenshot ->
                deleteScreenshot(video.id, screenshot)
            }
        } else {
            logger.d("Video ${video.path} has no screenshots")
        }
        database.videoQueries.delete(video.id)
    }

    override suspend fun captureScreenshots(
        video: Video,
        numberOfScreenshots: Int,
    ): Video {
        logger.d("Getting screenshots for videoId $video")
        val file = File(video.path)
        val videoDuration = getVideoDuration(video.id)
        val timestamps = getTimestamps(numberOfScreenshots, videoDuration)

        val screenshots = timestamps.mapIndexed { index: Int, timestamp: Long ->
            val screenshotFile = screenshotCapturing.captureScreenshot(file, timestamp, index)
            screenshotFile.absolutePath
        }

        val newVideo = video.copy(screenshots = screenshots)
        database.videoQueries.upsert(newVideo)
        return newVideo
    }

    override suspend fun filterVideos(files: List<*>) {
        logger.d("Getting videos from files")
        val videos = fileProcessor.filterVideoFiles(files)
        val localVideos = database.videoQueries.getAll().executeAsList()
        val filteredVideos = videos.filter {
            it.path !in localVideos.map { video -> video.path }
        }
        filteredVideos.forEach { video ->
            val videoName = video.nameWithoutExtension
                .replace("_", " ")
            logger.d("Storing video ${video.absolutePath} | youtubeVideoId: $videoName")

            withContext(Dispatchers.IO) {
                database.videoQueries.upsert(
                    Video(
                        id = uuid4().toString(),
                        path = video.absolutePath,
                        screenshots = emptyList(),
                        descriptions = emptyList(),
                        descriptionContext = null,
                        contentInfo = ContentInfo(),
                        youtubeId = videoName,
                        isCompleted = false,
                        createdAt = Clock.System.now(),
                        modifiedAt = Clock.System.now(),
                    )
                )
            }
        }
    }

    private fun deleteScreenshot(videoId: String, screenshotPath: String) {
        val video = getVideoById(videoId)
        val screenshot = video.screenshots.find { it == screenshotPath }
        if (screenshot == null) {
            logger.d("Screenshot $screenshotPath not found")
            return
        }
        logger.d("Deleting screenshot $screenshot")
        if (screenshot !in video.screenshots) return

        val updatedScreenshots = video.screenshots - screenshot
        val updatedVideo = video.copy(
            screenshots = updatedScreenshots,
            modifiedAt = Clock.System.now()
        )
        database.videoQueries.upsert(updatedVideo)
        fileProcessor.deleteFile(screenshot)
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
