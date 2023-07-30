package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.ProcessingConfig
import com.charleex.vidgenius.datasource.ProcessingState
import com.charleex.vidgenius.datasource.VideoProcessing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class VideoProcessingDebug: VideoProcessing {
    override fun getVideoIds(): Flow<List<String>> = flowOf(listOf("1", "2", "3"))

    override suspend fun filterVideosFromFiles(files: List<*>) {
    }

    override fun processVideo(videoId: String, config: ProcessingConfig): Flow<ProcessingState> =
        flowOf(ProcessingState.Done)
}
