package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.model.UploadItem
import com.charleex.vidgenius.yt.VisionAiService
import com.charleex.vidgenius.yt.youtube.model.ChannelUploadsItem
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

interface GoogleCloudRepository {
    suspend fun getTextFromImage(videoId: String): Flow<Float>
}

internal class GoogleCloudRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val visionAiService: VisionAiService,
) : GoogleCloudRepository {
    override suspend fun getTextFromImage(videoId: String): Flow<Float> = flow {
        logger.d("Getting screenshots from video $videoId")
        val video = database.videoQueries.getById(videoId).executeAsOne()
        val screenshots = video.screenshots
        logger.d { "Screenshots: ${screenshots.size}" }
        val screenshotsWithDescription = screenshots.mapIndexed { index, screenshot ->
            val text = visionAiService.getTextFromImage(screenshot.path)
            logger.d { "Text from screenshot ${screenshot.path}: \n$text" }
            val screenshotWithDescription = screenshot.copy(
                description = text,
                modifiedAt = Clock.System.now(),
            )
            emit((index + 1).toFloat() / screenshots.size)
            screenshotWithDescription
        }
        val newVideo = video.copy(screenshots = screenshotsWithDescription)
        database.videoQueries.upsert(newVideo)
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