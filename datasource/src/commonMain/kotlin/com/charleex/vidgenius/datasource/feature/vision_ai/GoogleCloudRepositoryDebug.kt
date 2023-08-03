package com.charleex.vidgenius.datasource.feature.vision_ai

import com.charleex.vidgenius.datasource.db.Video
import kotlinx.coroutines.delay

internal class GoogleCloudRepositoryDebug : GoogleCloudRepository {
    override suspend fun getTextFromImages(video: Video): Video {
        delay(1000)
        return video
    }
}
