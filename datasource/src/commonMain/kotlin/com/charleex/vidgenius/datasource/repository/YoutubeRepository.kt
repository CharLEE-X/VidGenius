package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.datasource.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.datasource.youtube.video.ChannelUploadsService
import com.charleex.vidgenius.datasource.youtube.video.UploadVideoService
import com.charleex.vidgenius.datasource.youtube.youtube.YoutubeConfig
import kotlinx.coroutines.delay
import java.io.File

interface YoutubeRepository {
    suspend fun getYtChannelUploads(): List<UploadItem>
    suspend fun getYtVideoDetail(videoId: String): UploadItem
    suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String

    suspend fun switchConfig(index: Int)
    fun login()
    fun logOut()
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

    override fun login() {
        googleAuth.authorize(YoutubeConfig.UploadVideo.scope, "uploadvideo", 2)
    }

    override fun logOut() {
        googleAuth.logOut("uploadvideo")
    }

    override suspend fun switchConfig(index: Int) {
        val credentialDatastore = "uploadvideo"
        googleAuth.logOut(credentialDatastore)
        delay(100)
        googleAuth.authorize(YoutubeConfig.UploadVideo.scope, credentialDatastore, index)
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
