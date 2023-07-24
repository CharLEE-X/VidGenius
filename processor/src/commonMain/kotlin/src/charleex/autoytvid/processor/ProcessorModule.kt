package src.charleex.autoytvid.processor

import co.touchlab.kermit.Logger.Companion.withTag
import org.koin.dsl.module
import src.charleex.autoytvid.processor.file.FileProcessor
import src.charleex.autoytvid.processor.file.FileProcessorImpl
import src.charleex.autoytvid.processor.screenshot.VideoScreenshotCapturing
import src.charleex.autoytvid.processor.screenshot.VideoScreenshotCapturingImpl

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
