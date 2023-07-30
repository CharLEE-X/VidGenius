package com.charleex.vidgenius.datasource.di

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.GoogleCloudRepository
import com.charleex.vidgenius.datasource.GoogleCloudRepositoryImpl
import com.charleex.vidgenius.datasource.YoutubeRepository
import com.charleex.vidgenius.datasource.YoutubeRepositoryImpl
import com.charleex.vidgenius.datasource.OpenAiRepository
import com.charleex.vidgenius.datasource.OpenAiRepositoryImpl
import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.datasource.VideoRepositoryImpl
import com.charleex.vidgenius.vision_ai.visionAiModule
import com.charleex.vidgenius.youtube.youtubeModule
import org.koin.dsl.module
import src.charleex.vidgenius.api.apiModule
import src.charleex.vidgenius.processor.processorModule
import src.charleex.vidgenius.whisper.openAiModule
import java.io.File

val repositoryModule = module {
    val appDataDir = createAppDataDir()

    includes(
        platformModule(appDataDir),
        processorModule(appDataDir),
        youtubeModule(),
        visionAiModule(),
        openAiModule,
        apiModule,
        settingsModule,
        databaseModule,
    )
    single<GoogleCloudRepository> {
        GoogleCloudRepositoryImpl(
            logger = withTag(GoogleCloudRepository::class.simpleName!!),
            database = get(),
            visionAiService = get(),
        )
    }
    single<OpenAiRepository> {
        OpenAiRepositoryImpl(
            montoApi = get(),
            database = get(),
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
    single<VideoRepository> {
        VideoRepositoryImpl(
            logger = withTag(VideoRepository::class.simpleName!!),
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
