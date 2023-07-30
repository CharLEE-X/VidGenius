package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.youtube.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.youtube.youtube.video.ChannelUploadsService
import com.charleex.vidgenius.youtube.youtube.video.UploadVideoProgress
import com.charleex.vidgenius.youtube.youtube.video.UploadVideoService
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File

interface YoutubeRepository {
    suspend fun getYtChannelUploads(): List<UploadItem>
    suspend fun getYtVideoDetail(videoId: String): UploadItem
    suspend fun getYoutubeVideoLink(videoId: String): String
    suspend fun uploadVideo(
        videoId: String,
        channelId: String,
    ): Flow<Float>
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val channelUploadsService: ChannelUploadsService,
    private val uploadVideoService: UploadVideoService,
) : YoutubeRepository {
    override suspend fun getYtChannelUploads(): List<UploadItem> {
        logger.d { "Getting channel uploads" }
        return channelUploadsService.getUploadList().toUploadItems()
    }

    override suspend fun getYtVideoDetail(videoId: String): UploadItem {
        logger.d { "Getting video detail $videoId" }
        return channelUploadsService.getVideoDetail(videoId).toUploadItem()
    }

    override suspend fun getYoutubeVideoLink(videoId: String): String {
        val video: Video = database.videoQueries.getById(videoId).executeAsOneOrNull() ?: error("Video not found")
        val youtubeVideoId = video.youtubeVideoId ?: error("Youtube video id not found")
        return "https://www.youtube.com/watch?v=$youtubeVideoId"
    }

    override suspend fun uploadVideo(
        videoId: String,
        channelId: String,
    ): Flow<Float> {
        val video: Video = database.videoQueries.getById(videoId).executeAsOneOrNull() ?: run {
            logger.e { "Video not found" }
            return flowOf(0f)

        }
        val file = File(video.path)
        if (!file.exists()) error("File not found")

        return uploadVideoService.uploadVideo(
            videoFile = file,
            title = video.title ?: file.nameWithoutExtension,
            description = video.description ?: "no description",
            tags = video.tags,
            channelId = channelId,
        ).map { progress ->
            val value = when (progress) {
                is UploadVideoProgress.Error -> 0f
                is UploadVideoProgress.Progress -> progress.progress
                is UploadVideoProgress.Success -> {
                    val updatedVideo = video.copy(
                        youtubeVideoId = progress.youtubeVideoId,
                    )
                    database.videoQueries.upsert(updatedVideo)
                    1f
                }
            }
            logger.d { "Progress: $progress" }
            value
        }
    }
}

private fun List<ChannelUploadsItem>.toUploadItems(): List<UploadItem> {
    return map { it.toUploadItem() }
}

private fun ChannelUploadsItem.toUploadItem(): UploadItem {
    return UploadItem(
        id = this.videoId,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
    )
}
