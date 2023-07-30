package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.GoogleCloudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getTextFromImage(videoId: String): Flow<Float> {
        return flowOf(1f)
    }
}
