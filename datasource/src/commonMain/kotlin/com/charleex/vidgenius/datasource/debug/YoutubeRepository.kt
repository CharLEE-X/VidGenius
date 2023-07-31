package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import kotlinx.datetime.Instant

internal class YoutubeRepositoryDebug : YoutubeRepository {
    override suspend fun getYtChannelUploads(): List<UploadItem> = emptyList()

    override suspend fun getYtVideoDetail(videoId: String): UploadItem = UploadItem(
        id = "debug id",
        title = "debug title",
        description = "debug description",
        publishedAt = Instant.fromEpochMilliseconds(0L),
    )

    override suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String = "flowOf(1f)"
}
