package com.charleex.vidgenius.datasource.feature.youtube

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant

internal class YoutubeRepositoryDebug() : YoutubeRepository {
    override val isFetchingUploads: StateFlow<Boolean> = flowOf(false) as StateFlow<Boolean>

    private var size = 4
    override fun flowOfYtVideos(): Flow<List<YtVideo>> {
        val yt1 = YtVideo(
            id = "Sequence 02_1",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "public",
            hasMultiLanguage = true,
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt2 = YtVideo(
            id = "Sequence 02_2",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "draft",
            hasMultiLanguage = false,
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt3 = YtVideo(
            id = "Sequence 02_3",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "public",
            hasMultiLanguage = true,
            publishedAt = Instant.DISTANT_FUTURE
        )
        val yt4 = YtVideo(
            id = "Sequence 02_4",
            title = "Video 1",
            description = "Description 1",
            tags = listOf("tag1", "tag2"),
            privacyStatus = "draft",
            hasMultiLanguage = true,
            publishedAt = Instant.DISTANT_FUTURE
        )
        val all = listOf(yt1, yt2, yt3, yt4)
        return flowOf(
            all.take(size)
        )
    }

    override suspend fun fetchUploads() {
        size = (1..4).random()
    }

    override suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean {
        return true
    }

    override fun signOut() {
    }
}
