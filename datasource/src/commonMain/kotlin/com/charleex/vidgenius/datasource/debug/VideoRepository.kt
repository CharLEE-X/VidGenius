package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.datasource.db.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

internal class VideoRepositoryDebug : VideoRepository {
    override suspend fun filterVideos(files: List<*>) {}

    override suspend fun captureScreenshots(videoId: String, timestamps: List<Long>): Flow<Float> = flowOf(1f)

    override fun getVideoDuration(videoId: String): Long = 1L

    override fun flowOfVideo(videoId: String): Flow<Video> = flowOf(
        Video(
            id = "1",
            path = "path",
            title = "title",
            description = "description",
            duration = 1L,
            screenshots = emptyList(),
            createdAt = Clock.System.now(),
            modifiedAt = Clock.System.now(),
            tags = emptyList(),
            youtubeVideoId = "youtubeId",
        )
    )

    override fun flowOfVideos(): Flow<List<Video>> = flowOf(emptyList())

    override fun deleteVideo(videoId: String) {}

    override fun deleteScreenshot(videoId: String, screenshotPath: String) {}
}
