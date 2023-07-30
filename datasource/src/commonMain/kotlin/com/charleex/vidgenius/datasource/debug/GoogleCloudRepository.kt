package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getTextFromImage(videoId: String): String {
        return "flowOf(1f)"
    }
}
