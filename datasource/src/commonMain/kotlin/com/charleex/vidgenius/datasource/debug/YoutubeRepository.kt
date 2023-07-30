package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.charleex.vidgenius.datasource.model.UploadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
        videoId: String,
        channelId: String,
    ): Flow<Float> = flowOf(1f)
}
