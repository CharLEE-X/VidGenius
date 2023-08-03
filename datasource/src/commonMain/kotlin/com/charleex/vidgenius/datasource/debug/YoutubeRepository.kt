package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.YtUploadItem
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant

internal class YoutubeRepositoryDebug : YoutubeRepository {
    override suspend fun getYtChannelUploads(): List<YtUploadItem> = emptyList()

    override suspend fun getYtVideoDetail(videoId: String): YtUploadItem = YtUploadItem(
        id = "debug id",
        title = "debug title",
        description = "debug description",
        publishedAt = Instant.fromEpochMilliseconds(0L),
    )

    override suspend fun uploadVideo(
        video: Video,
        channelId: String,
    ): String {
        delay(1000)
        return "flowOf(1f)"
    }
}
