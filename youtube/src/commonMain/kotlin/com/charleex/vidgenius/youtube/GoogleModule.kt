package com.charleex.vidgenius.youtube

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.youtube.auth.GoogleAuth
import com.charleex.vidgenius.youtube.auth.GoogleAuthImpl
import com.charleex.vidgenius.youtube.youtube.YoutubeConfig
import com.charleex.vidgenius.youtube.youtube.video.ChannelUploadsService
import com.charleex.vidgenius.youtube.youtube.video.ChannelUploadsServiceImpl
import com.charleex.vidgenius.youtube.youtube.video.UpdateVideoService
import com.charleex.vidgenius.youtube.youtube.video.UpdateVideoServiceImpl
import com.charleex.vidgenius.youtube.youtube.video.UploadVideoService
import com.charleex.vidgenius.youtube.youtube.video.UploadVideoServiceImpl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

private const val CREDENTIALS_DIRECTORY = ".oauth-credentials"

fun youtubeModule() = module {
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
            logger = withTag(GoogleAuth::class.simpleName!!),
            httpTransport = get(),
            jsonFactory = get(),
            credentialDirectory = CREDENTIALS_DIRECTORY,
        )
    }
    single<ChannelUploadsService> {
        val googleAuth = get<GoogleAuth>()
        val credential = googleAuth.authorize(
            YoutubeConfig.UploadList.scope,
            YoutubeConfig.UploadList.STORE
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UploadList.NAME) }
        ChannelUploadsServiceImpl(
            logger = withTag(ChannelUploadsService::class.simpleName!!),
            youtube = youtube,
        )
    }
    single<UploadVideoService> {
        val googleAuth = get<GoogleAuth>()
        val credential = googleAuth.authorize(
            YoutubeConfig.UploadVideo.scope,
            YoutubeConfig.UploadVideo.STORE
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UploadVideo.NAME) }
        UploadVideoServiceImpl(
            logger = withTag(UploadVideoService::class.simpleName!!),
            youtube = youtube,
        )
    }
    single<UpdateVideoService> {
        val googleAuth = get<GoogleAuth>()
        val credential = googleAuth.authorize(
            YoutubeConfig.UpdateVideo.scope,
            YoutubeConfig.UpdateVideo.STORE,
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UpdateVideo.NAME) }
        UpdateVideoServiceImpl(
            logger = withTag(UpdateVideoService::class.simpleName!!),
            youtube = youtube,
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
}

