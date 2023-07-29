package src.charleex.vidgenius.processor

import co.touchlab.kermit.Logger.Companion.withTag
import org.koin.dsl.module
import src.charleex.vidgenius.processor.file.FileProcessor
import src.charleex.vidgenius.processor.file.FileProcessorImpl
import src.charleex.vidgenius.processor.screenshot.ScreenshotCapturing
import src.charleex.vidgenius.processor.screenshot.ScreenshotCapturingImpl
import java.io.File

fun processorModule(appDataDir: File) = module {
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
}
