package com.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger
import org.koin.dsl.module
import java.io.File

fun videoFileModule(appDataDir: File) = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = Logger.withTag(FileProcessor::class.simpleName!!),
        )
    }
    single<ScreenshotCapturing> {
        ScreenshotCapturingImpl(
            logger = Logger.withTag(ScreenshotCapturing::class.simpleName!!),
            appDataDirFile = appDataDir,
        )
    }

    single<LocalVideoProcessor> {
        LocalVideoProcessorImpl(
            logger = Logger.withTag(LocalVideoProcessor::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
        )
    }
}
