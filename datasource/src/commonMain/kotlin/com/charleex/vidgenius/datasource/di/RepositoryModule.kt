package com.charleex.vidgenius.datasource.di

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.AssistRepository
import com.charleex.vidgenius.datasource.AssistRepositoryImpl
import com.charleex.vidgenius.datasource.ScreenshotRepository
import com.charleex.vidgenius.datasource.ScreenshotRepositoryImpl
import com.charleex.vidgenius.datasource.YoutubeRepository
import com.charleex.vidgenius.datasource.YoutubeRepositoryImpl
import com.charleex.vidgenius.yt.youtubeModule
import org.koin.dsl.module
import src.charleex.vidgenius.api.apiModule
import src.charleex.vidgenius.processor.processorModule
import src.charleex.vidgenius.whisper.whisperModule
import java.io.File


val repositoryModule = module {
    val appDataDir = createAppDataDir()

    includes(
        platformModule(appDataDir),
        whisperModule,
        apiModule,
        processorModule(appDataDir),
        settingsModule,
        databaseModule,
        youtubeModule(),
    )
    single<AssistRepository> {
        AssistRepositoryImpl(
            montoApi = get(),
            transcriptionService = get(),
            translationService = get(),
            chatService = get(),
        )
    }
    single<YoutubeRepository> {
        YoutubeRepositoryImpl(
            logger = withTag(YoutubeRepository::class.simpleName!!),
            channelUploadsService = get(),
        )
    }
    single<ScreenshotRepository> {
        ScreenshotRepositoryImpl(
            logger = withTag(ScreenshotRepository::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
            database = get(),
        )
    }
}

private fun createAppDataDir(): File {
    val userHomeDir = System.getProperty("user.home")
    val appDataDir = File(userHomeDir, "VidGeniusAppData")
    if (!appDataDir.exists()) {
        appDataDir.mkdir()
    }
    return appDataDir
}
