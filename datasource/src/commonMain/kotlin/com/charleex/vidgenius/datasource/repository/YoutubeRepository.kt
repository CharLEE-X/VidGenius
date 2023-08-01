package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.youtube.auth.GoogleAuth
import com.charleex.vidgenius.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.youtube.video.ChannelUploadsService
import com.charleex.vidgenius.youtube.video.UploadVideoService
import com.charleex.vidgenius.youtube.youtube.YoutubeConfig
import java.io.File

interface YoutubeRepository {
    suspend fun getYtChannelUploads(): List<UploadItem>
    suspend fun getYtVideoDetail(videoId: String): UploadItem
    suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String

    fun logOut(credentialStore: String)
    fun signIn(uploadStore: String)
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val googleAuth: GoogleAuth,
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

    override fun logOut(credentialStore: String) {
        googleAuth.logOut(credentialStore)
    }

    override fun signIn(uploadStore: String) {
        googleAuth.authorize(YoutubeConfig.UploadVideo.scope, uploadStore)
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
