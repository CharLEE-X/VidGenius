package src.charleex.vidgenius.processor

import co.touchlab.kermit.Logger.Companion.withTag
import org.koin.dsl.module
import src.charleex.vidgenius.processor.file.FileProcessor
import src.charleex.vidgenius.processor.file.FileProcessorImpl
import src.charleex.vidgenius.processor.screenshot.VideoScreenshotCapturing
import src.charleex.vidgenius.processor.screenshot.VideoScreenshotCapturingImpl

val processorModule = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = withTag(FileProcessor::class.simpleName!!),
        )
    }
    single<VideoScreenshotCapturing> {
        VideoScreenshotCapturingImpl(
            logger = withTag(VideoScreenshotCapturing::class.simpleName!!),
        )
    }
}
