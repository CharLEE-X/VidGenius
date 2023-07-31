package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.ProcessingConfig
import com.charleex.vidgenius.datasource.ProcessingState
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.db.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class VideoProcessingDebug: VideoProcessing {
    override fun getVideos(): Flow<List<Video>> = flowOf(listOf())

    override suspend fun filterVideosFromFiles(files: List<*>) {
    }

    override fun processVideo(videoId: String, config: ProcessingConfig): Flow<ProcessingState> =
        flowOf(ProcessingState.Done)
}
