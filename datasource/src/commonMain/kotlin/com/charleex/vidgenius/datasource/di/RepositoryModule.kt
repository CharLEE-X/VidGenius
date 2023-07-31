package com.charleex.vidgenius.datasource.di

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.VideoProcessingImpl
import com.charleex.vidgenius.datasource.debug.GoogleCloudRepositoryDebug
import com.charleex.vidgenius.datasource.debug.OpenAiRepositoryDebug
import com.charleex.vidgenius.datasource.debug.YoutubeRepositoryDebug
import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import com.charleex.vidgenius.datasource.repository.GoogleCloudRepositoryImpl
import com.charleex.vidgenius.datasource.repository.OpenAiRepository
import com.charleex.vidgenius.datasource.repository.OpenAiRepositoryImpl
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.datasource.repository.VideoRepositoryImpl
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.charleex.vidgenius.datasource.repository.YoutubeRepositoryImpl
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
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


    single<VideoRepository> {
        VideoRepositoryImpl(
            logger = withTag(VideoRepository::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
            database = get(),
        )
    }

    single<VideoProcessing> {
        VideoProcessingImpl(
            logger = withTag(VideoProcessing::class.simpleName!!),
            database = get(),
            videoRepository = get(),
            openAiRepository = get(),
            googleCloudRepository = get(),
            youtubeRepository = get(),
        )
    }

    single<GoogleCloudRepository> {
        if (getIsDebugBuild()) GoogleCloudRepositoryDebug().also { println("GoogleCloudRepository in DEBUG mode") }
        else GoogleCloudRepositoryImpl(
            logger = withTag(GoogleCloudRepository::class.simpleName!!),
            visionAiService = get(),
        ).also { println("GoogleCloudRepository in RELEASE mode") }
    }

    single<OpenAiRepository> {
        if (getIsDebugBuild()) OpenAiRepositoryDebug().also { println("OpenAiRepository in DEBUG mode") }
        else OpenAiRepositoryImpl(
            logger = withTag(OpenAiRepository::class.simpleName!!),
            montoApi = get(),
            transcriptionService = get(),
            translationService = get(),
            chatService = get(),
        ).also { println("OpenAiRepository in RELEASE mode") }
    }
    single<YoutubeRepository> {
        if (getIsDebugBuild()) YoutubeRepositoryDebug().also { println("YoutubeRepository in DEBUG mode") }
        else YoutubeRepositoryImpl(
            logger = withTag(YoutubeRepository::class.simpleName!!),
            channelUploadsService = get(),
            uploadVideoService = get(),
        ).also { println("YoutubeRepository in RELEASE mode") }
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
