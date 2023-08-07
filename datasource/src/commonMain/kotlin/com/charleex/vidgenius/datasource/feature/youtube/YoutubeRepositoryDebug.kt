package com.charleex.vidgenius.datasource.feature.youtube

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant

internal class YoutubeRepositoryDebug() : YoutubeRepository {
    private var size = 4
    override fun flowOfYtVideos(): Flow<List<YtVideo>> {
        val yt1 = YtVideo(
            id = "Sequence 02_1",
            channelId = "UCXgGY0wkgOzynnHvSEVmE3A",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "public",
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt2 = YtVideo(
            id = "Sequence 02_2",
            channelId = "UCXgGY0wkgOzynnHvSEVmE3A",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "draft",
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt3 = YtVideo(
            id = "Sequence 02_3",
            channelId = "UCXgGY0wkgOzynnHvSEVmE3A",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "public",
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt4 = YtVideo(
            id = "Sequence 02_4",
            channelId = "UCXgGY0wkgOzynnHvSEVmE3A",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "draft",
            publishedAt = Instant.DISTANT_FUTURE
        )
        val all = listOf(yt1, yt2, yt3, yt4)
        return flowOf(
            all.take(size)
        )
    }

    override suspend fun fetchUploads() {
        delay(1000)
        size = (1..4).random()
    }

    override suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean {
        return true
    }

    override fun signOut() {
    }
}
