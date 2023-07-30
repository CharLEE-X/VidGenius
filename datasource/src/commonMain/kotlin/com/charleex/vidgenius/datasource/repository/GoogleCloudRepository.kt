package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.vision_ai.VisionAiService

interface GoogleCloudRepository {
    suspend fun getTextFromImage(screenshotPath: String): String
}

internal class GoogleCloudRepositoryImpl(
    private val logger: Logger,
    private val visionAiService: VisionAiService,
) : GoogleCloudRepository {
    override suspend fun getTextFromImage(screenshotPath: String): String {
        logger.d("Getting screenshot for $screenshotPath")
        return visionAiService.getTextFromImage(screenshotPath)
    }
}

