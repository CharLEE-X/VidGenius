package com.charleex.vidgenius.vision_ai

import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import java.io.File

interface GoogleCloudRepository {
    suspend fun getDescriptionsFromScreenshots(screenshots: List<String>): List<String>
}

internal class GoogleCloudRepositoryImpl(
    private val logger: Logger,
    private val visionAiApi: VisionAiApi,
) : GoogleCloudRepository {
    companion object{
        internal const val MIN_SCORE = 0.45f
    }
    override suspend fun getDescriptionsFromScreenshots(screenshots: List<String>): List<String> {
        val descriptions = screenshots.map { screenshotPath ->
            logger.d("Getting description for screenshot $screenshotPath")
            val imageFile = File(screenshotPath)
            if (!imageFile.exists()) {
                throw NoSuchFileException(
                    file = imageFile,
                    reason = "The file you specified does not exist"
                )
            }
            visionAiApi.fetchTextFromImage(imageFile)
                .filter { it.key >= MIN_SCORE }
                .toList()
                .sortedByDescending { it.first }
                .take(3)
                .joinToString(separator = " ") { it.second }
        }
        logger.d("Descriptions $descriptions")
        return descriptions
    }
}

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getDescriptionsFromScreenshots(screenshots: List<String>): List<String> {
        delay(1000)
        return emptyList()
    }
}
