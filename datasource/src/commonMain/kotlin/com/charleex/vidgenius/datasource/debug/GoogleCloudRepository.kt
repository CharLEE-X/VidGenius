package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import kotlinx.coroutines.delay

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getTextFromImage(screenshotPath: String): String {
        delay(1000)
        return "flowOf(1f)"
    }
}
