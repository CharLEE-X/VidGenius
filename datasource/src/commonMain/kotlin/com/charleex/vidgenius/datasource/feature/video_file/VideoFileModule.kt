package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.VideoProcessingImpl
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessorImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

internal fun videoFileModule(appDataDir: File) = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = Logger.withTag(FileProcessor::class.simpleName!!),
        )
    }
    single<ScreenshotCapturing> {
        ScreenshotCapturingImpl(
            logger = Logger.withTag(ScreenshotCapturing::class.simpleName!!),
            appDataDir = appDataDir,
        )
    }
    single<VideoProcessing> {
        VideoProcessingImpl(
            logger = Logger.withTag(VideoProcessing::class.simpleName!!),
            database = get(),
            videoFileRepository = get(),
            openAiRepository = get(),
            googleCloudRepository = get(),
            youtubeRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
}
