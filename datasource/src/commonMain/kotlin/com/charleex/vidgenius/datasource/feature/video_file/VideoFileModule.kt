package com.charleex.vidgenius.datasource.feature.video_file

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessorImpl
import com.charleex.vidgenius.datasource.model.allChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal fun videoFileModule(appDataDir: File) = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = Logger.withTag(FileProcessor::class.simpleName!!),
        )
    }

    allChannels.forEach { channel ->
        single<ScreenshotCapturing>(named(channel.id)) {
            ScreenshotCapturingImpl(
                logger = Logger.withTag(ScreenshotCapturing::class.simpleName!!),
                appDataDir = appDataDir,
                channelId = channel.id
            )
        }

        single<VideoFileRepository>(named(channel.id)) {
            VideoFileRepositoryImpl(
                logger = Logger.withTag(VideoFileRepository::class.simpleName!!),
                channel = channel,
                database = get(),
                fileProcessor = get(),
                screenshotCapturing = get(named(channel.id)),
                scope = CoroutineScope(Dispatchers.Default),
            )
        }
    }
}
