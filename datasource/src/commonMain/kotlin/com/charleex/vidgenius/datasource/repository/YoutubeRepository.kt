package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.YtUploadItem
import com.charleex.vidgenius.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.youtube.video.MyUploadsService
import com.charleex.vidgenius.youtube.video.UploadVideoService
import java.io.File

interface YoutubeRepository {
    suspend fun getYtChannelUploads(): List<YtUploadItem>
    suspend fun getYtVideoDetail(videoId: String): YtUploadItem
    suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val myUploadsService: MyUploadsService,
    private val uploadVideoService: UploadVideoService,
) : YoutubeRepository {
    override suspend fun getYtChannelUploads(): List<YtUploadItem> {
        logger.d { "Getting channel uploads" }
        return myUploadsService.getUploadList().map { it.toUploadItem() }
    }

    override suspend fun getYtVideoDetail(videoId: String): YtUploadItem {
        logger.d { "Getting video detail $videoId" }
        return myUploadsService.getVideoDetail(videoId).toUploadItem()
    }

    override suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String {
        val file = File(video.path)
        if (!file.exists()) error("File not found")

        return uploadVideoService.uploadVideo(
            videoFile = file,
            title = video.title ?: file.nameWithoutExtension,
            description = video.description ?: "no description",
            tags = video.tags,
            channelId = channelId,
        )
    }
}

private fun ChannelUploadsItem.toUploadItem(): YtUploadItem {
    return YtUploadItem(
        id = this.videoId,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
    )
}
