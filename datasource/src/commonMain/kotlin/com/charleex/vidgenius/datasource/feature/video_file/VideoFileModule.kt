package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.VideoProcessingImpl
import org.koin.dsl.module
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessorImpl
import com.charleex.vidgenius.datasource.feature.video_file.ScreenshotCapturingImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

fun videoFileModule(appDataDir: File) = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = withTag(FileProcessor::class.simpleName!!),
        )
    }
    single<ScreenshotCapturing> {
        ScreenshotCapturingImpl(
            logger = withTag(ScreenshotCapturing::class.simpleName!!),
            appDataDir = appDataDir,
        )
    }
    single<VideoProcessing> {
        VideoProcessingImpl(
            logger = withTag(VideoProcessing::class.simpleName!!),
            database = get(),
            videoFileRepository = get(),
            openAiRepository = get(),
            googleCloudRepository = get(),
            youtubeRepository = get(),
        )
    }
}
