package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.yt.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.yt.youtube.video.ChannelUploadsService

interface YoutubeRepository {
    suspend fun getYtChannelUploads(): List<UploadItem>
    suspend fun getYtVideoDetail(videoId: String): UploadItem
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val channelUploadsService: ChannelUploadsService,
) : YoutubeRepository {
    override suspend fun getYtChannelUploads(): List<UploadItem> {
        logger.d { "Getting channel uploads" }
        return channelUploadsService.getUploadList().toUploadItems()
    }

    override suspend fun getYtVideoDetail(videoId: String): UploadItem {
        logger.d { "Getting video detail $videoId" }
        return channelUploadsService.getVideoDetail(videoId).toUploadItem()
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
