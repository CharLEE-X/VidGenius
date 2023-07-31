package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

internal class VideoRepositoryDebug : VideoRepository {
    override fun getVideoById(videoId: String): Video {
        return Video(
            id = "1",
            path = "path",
            screenshots = emptyList(),
            descriptions = listOf("description"),
            descriptionContext = "descriptionContext",
            title = "title",
            description = "description",
            tags = emptyList(),
            youtubeVideoId = "youtubeId",
            createdAt = Clock.System.now(),
            modifiedAt = Clock.System.now(),
        )
    }

    override suspend fun filterVideos(files: List<*>) {}

    override fun flowOfVideos(): Flow<List<Video>> = flowOf(
        listOf(
            Video(
                id = "1",
                path = "path",
                screenshots = emptyList(),
                descriptions = listOf("description"),
                descriptionContext = "descriptionContext",
                title = "title",
                description = "description",
                tags = emptyList(),
                youtubeVideoId = "youtubeId",
                createdAt = Clock.System.now(),
                modifiedAt = Clock.System.now(),
            )
        )
    )

    override fun deleteVideo(videoId: String) {}

    override suspend fun captureScreenshots(
        videoId: String,
        numberOfScreenshots: Int,
        onProgress: suspend (Float) -> Unit,
    ) {
        onProgress(1f)
    }
}
