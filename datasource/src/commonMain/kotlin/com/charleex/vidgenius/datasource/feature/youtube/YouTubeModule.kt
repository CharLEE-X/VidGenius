package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.ConfigManager
import com.charleex.vidgenius.datasource.ConfigManagerImpl
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuthImpl
import com.charleex.vidgenius.datasource.model.allChannels
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsService
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoService
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.video.UploadVideoService
import com.charleex.vidgenius.datasource.feature.youtube.video.UploadVideoServiceImpl
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

private const val CREDENTIALS_DIRECTORY = ".oauth-credentials"

internal fun youtubeModule(appDataDir: File) = module {
    single<HttpTransport> { NetHttpTransport() }
    single<JsonFactory> { JacksonFactory() }

    allChannels.forEach { channel ->
        single<GoogleAuth>(named(channel.id)) {
            GoogleAuthImpl(
                logger = Logger.withTag(GoogleAuth::class.simpleName!!),
                appDataDir = appDataDir.absolutePath,
                channel = channel,
                httpTransport = get(),
                jsonFactory = get(),
                credentialDirectory = CREDENTIALS_DIRECTORY,
            )
        }

        single<MyUploadsService>(named(channel.id)) {
            MyUploadsServiceImpl(
                logger = Logger.withTag(MyUploadsService::class.simpleName!!),
                googleAuth = get(named(channel.id)),
                httpTransport = get(),
                jsonFactory = get(),
            )
        }
        single<UploadVideoService>(named(channel.id)) {
            UploadVideoServiceImpl(
                logger = Logger.withTag(UpdateVideoService::class.simpleName!!),
                googleAuth = get(named(channel.id)),
                httpTransport = get(),
                jsonFactory = get(),
            )
        }

        single<UpdateVideoService>(named(channel.id)) {
            UpdateVideoServiceImpl(
                logger = Logger.withTag(UpdateVideoService::class.simpleName!!),
                googleAuth = get(named(channel.id)),
                httpTransport = get(),
                jsonFactory = get(),
            )
        }

        single<YoutubeRepository>(named(channel.id)) {
            if (getIsDebugBuild())
                YoutubeRepositoryDebug().also { println("YoutubeRepository in DEBUG mode") }
            else YoutubeRepositoryImpl(
                logger = Logger.withTag(YoutubeRepository::class.simpleName!!),
                channelId = channel.id,
                database = get(),
                googleAuth = get(named(channel.id)),
                myUploadsService = get(named(channel.id)),
                updateVideoService = get(named(channel.id)),
            ).also { println("YoutubeRepository in RELEASE mode") }
        }
    }
}
