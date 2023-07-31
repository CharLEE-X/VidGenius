package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getTextFromImage(screenshotPath: String): String {
        return "flowOf(1f)"
    }
}
