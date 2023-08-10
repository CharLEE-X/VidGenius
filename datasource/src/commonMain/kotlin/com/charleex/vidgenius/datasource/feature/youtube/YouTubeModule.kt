package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.ConfigManagerImpl
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuthImpl
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsService
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoService
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.video.UploadVideoService
import com.charleex.vidgenius.datasource.feature.youtube.video.UploadVideoServiceImpl
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

private const val CREDENTIALS_DIRECTORY = ".oauth-credentials"

internal fun youtubeModule(appDataDir: File) = module {
    single<HttpTransport> { NetHttpTransport() }
    single<JsonFactory> { JacksonFactory() }

    factory<YouTube> { param ->
        val credential: Credential = param[0]
        val applicationName: String = param[1]
        YouTube.Builder(
            get<HttpTransport>(),
            get<JsonFactory>(),
            credential
        )
            .setApplicationName(applicationName)
            .build()
    }

//    factory { param ->
//        val credential: Credential = param[0]
//        val applicationName: String = param[1]
//        YouTubeAnalytics
//            .Builder(
//                get<HttpTransport>(),
//                get<JsonFactory>(),
//                credential
//            )
//            .setApplicationName(applicationName)
//            .build()
//    }

    single<GoogleAuth> {
        GoogleAuthImpl(
            logger = Logger.withTag(GoogleAuth::class.simpleName!!),
            appDataDir = appDataDir.absolutePath,
            httpTransport = get(),
            jsonFactory = get(),
            credentialDirectory = CREDENTIALS_DIRECTORY,
        )
    }
    single<MyUploadsService> {
        MyUploadsServiceImpl(
            logger = Logger.withTag(MyUploadsService::class.simpleName!!),
            googleAuth = get(),
            httpTransport = get(),
            jsonFactory = get(),
        )
    }
    single<UploadVideoService> {
        UploadVideoServiceImpl(
            logger = Logger.withTag(UploadVideoService::class.simpleName!!),
            googleAuth = get(),
            httpTransport = get(),
            jsonFactory = get(),
        )
    }
    single<UpdateVideoService> {
        UpdateVideoServiceImpl(
            logger = Logger.withTag(UpdateVideoService::class.simpleName!!),
            googleAuth = get(),
            httpTransport = get(),
            jsonFactory = get(),
        )
    }
//    single<YouTubeAnalyticsReports> {
//        val googleAuth = get<GoogleAuth>()
//        val credential = googleAuth.authorize(
//            YoutubeConfig.YouTubeAnalyticsReports.scope,
//            YoutubeConfig.YouTubeAnalyticsReports.STORE,
//        )
//        val youtube =
//            get<YouTube> { parametersOf(credential, YoutubeConfig.YouTubeAnalyticsReports.NAME) }
//        val analytics = get<YouTubeAnalytics> {
//            parametersOf(
//                credential,
//                YoutubeConfig.YouTubeAnalyticsReports.NAME
//            )
//        }
//        YouTubeAnalyticsReportsImpl(
//            logger = withTag(YouTubeAnalyticsReports::class.simpleName!!),
//            youtube = youtube,
//            analytics = analytics,
//        )
//    }

    single<YoutubeRepository> {
        if (getIsDebugBuild())
            YoutubeRepositoryDebug().also { println("YoutubeRepository in DEBUG mode") }
        else YoutubeRepositoryImpl(
            logger = Logger.withTag(YoutubeRepository::class.simpleName!!),
            database = get(),
            googleAuth = get(),
            myUploadsService = get(),
            updateVideoService = get(),
            configManager = get(),
            scope = CoroutineScope(Dispatchers.Default),
        ).also { println("YoutubeRepository in RELEASE mode") }
    }
    single<ConfigManager> {
        ConfigManagerImpl(
            logger = Logger.withTag(ConfigManager::class.simpleName!!),
            database = get(),
            googleAuth = get(),
            scope = CoroutineScope(Dispatchers.Default),
        )
    }
}
