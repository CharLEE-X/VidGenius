package com.charleex.vidgenius.datasource.feature.vision_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video

interface GoogleCloudRepository {
    suspend fun getTextFromImages(video: Video): Video
}

internal class GoogleCloudRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val visionAiService: VisionAiService,
) : GoogleCloudRepository {
    override suspend fun getTextFromImages(video: Video): Video {
        val descriptions = video.screenshots.map {
            logger.d("Getting screenshot for $it")
            visionAiService.getTextFromImage(it)
        }
        logger.d("Descriptions $descriptions")
        val newVideo = video.copy(descriptions = descriptions)
        database.videoQueries.upsert(newVideo)
        return newVideo
    }
}

