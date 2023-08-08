package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessorImpl
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

    single<VideoFileRepository> {
        VideoFileRepositoryImpl(
            logger = Logger.withTag(VideoFileRepository::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
            database = get(),
        )
    }
}
