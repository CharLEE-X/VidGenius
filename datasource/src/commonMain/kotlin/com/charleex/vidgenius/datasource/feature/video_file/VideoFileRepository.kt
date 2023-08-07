package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.model.ChannelConfig
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File

interface VideoFileRepository {
    val isWatching: StateFlow<Boolean>
    fun getVideoById(videoId: String): Video
    fun flowOfVideos(channelId: String): Flow<List<Video>>
    fun deleteVideo(videoId: String)
    fun startWatchingDirectory(directory: String)
    fun stopWatchingDirectory(directory: String)

    suspend fun captureScreenshots(
        video: Video,
        numberOfScreenshots: Int,
        channelId: String,
    ): Video

    suspend fun filterVideos(paths: List<String>, channelId: String)
}

internal class VideoFileRepositoryImpl(
    private val logger: Logger,
    private val channel: ChannelConfig,
    private val database: VidGeniusDatabase,
    private val fileProcessor: FileProcessor,
    private val screenshotCapturing: ScreenshotCapturing,
    private val scope: CoroutineScope,
) : VideoFileRepository {
    private var watchJob: Job? = null

    private val _isWatching = MutableStateFlow(false)
    override val isWatching: StateFlow<Boolean> = _isWatching.asStateFlow()

    override fun getVideoById(videoId: String): Video {
        return database.videoQueries.getById(videoId).executeAsOne()
    }

    override fun flowOfVideos(channelId: String): Flow<List<Video>> {
        return database.videoQueries.getAllForChannel(channelId).asFlow()
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

    override fun startWatchingDirectory(directory: String) {
        logger.d("Watching directory $directory")
        watchJob = scope.launch {
            _isWatching.value = true
            fileProcessor.watchDirectory(directory)
        }
    }

    override fun stopWatchingDirectory(directory: String) {
        watchJob?.cancel()
        _isWatching.value = false
    }

    override suspend fun captureScreenshots(
        video: Video,
        numberOfScreenshots: Int,
        channelId: String,
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

    override suspend fun filterVideos(paths: List<String>, channelId: String) {
        logger.d("Getting videos from ${paths.size} files")
        val files = paths.map { File(it) }
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
                        channelId = channelId,
                        path = video.absolutePath,
                        screenshots = emptyList(),
                        descriptions = emptyList(),
                        descriptionContext = null,
                        title = null,
                        description = null,
                        tags = emptyList(),
                        youtubeName = videoName,
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
