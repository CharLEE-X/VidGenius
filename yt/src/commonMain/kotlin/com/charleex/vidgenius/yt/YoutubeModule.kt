package com.charleex.vidgenius.yt

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.yt.dev.devChannelUploadService
import com.charleex.vidgenius.yt.util.YoutubeConfig
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

private const val CREDENTIALS_DIRECTORY = ".oauth-credentials"


fun youtubeModule(isDevBuild: Boolean = false) = module {
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

    factory { param ->
        val credential: Credential = param[0]
        val applicationName: String = param[1]
        YouTubeAnalytics
            .Builder(
                get<HttpTransport>(),
                get<JsonFactory>(),
                credential
            )
            .setApplicationName(applicationName)
            .build()
    }

    single<YoutubeAuth> {
        YoutubeAuthImpl(
            logger = withTag(YoutubeAuth::class.simpleName!!),
            httpTransport = get(),
            jsonFactory = get(),
            credentialDirectory = CREDENTIALS_DIRECTORY,
        )
    }
    single<ChannelUploadsService> {
        if (isDevBuild) {
            devChannelUploadService
        } else {
            val youtubeAuth = get<YoutubeAuth>()
            val credential = youtubeAuth.authorize(
                YoutubeConfig.UploadList.scope,
                YoutubeConfig.UploadList.STORE
            )
            val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UploadList.NAME) }
            ChannelUploadsServiceImpl(
                logger = withTag(ChannelUploadsService::class.simpleName!!),
                youtube = youtube,
            )
        }
    }
    single<UploadVideo> {
        val youtubeAuth = get<YoutubeAuth>()
        val credential = youtubeAuth.authorize(
            YoutubeConfig.UploadVideo.scope,
            YoutubeConfig.UploadVideo.STORE
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UploadVideo.NAME) }
        UploadVideoImpl(
            logger = withTag(UploadVideo::class.simpleName!!),
            youtube = youtube,
        )
    }
    single<UpdateVideo> {
        val youtubeAuth = get<YoutubeAuth>()
        val credential = youtubeAuth.authorize(
            YoutubeConfig.UpdateVideo.scope,
            YoutubeConfig.UpdateVideo.STORE,
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.UpdateVideo.NAME) }
        UpdateVideoImpl(
            logger = withTag(UpdateVideo::class.simpleName!!),
            youtube = youtube,
        )
    }
    single<YouTubeAnalyticsReports> {
        val youtubeAuth = get<YoutubeAuth>()
        val credential = youtubeAuth.authorize(
            YoutubeConfig.YouTubeAnalyticsReports.scope,
            YoutubeConfig.YouTubeAnalyticsReports.STORE,
        )
        val youtube = get<YouTube> { parametersOf(credential, YoutubeConfig.YouTubeAnalyticsReports.NAME) }
        val analytics = get<YouTubeAnalytics> { parametersOf(credential, YoutubeConfig.YouTubeAnalyticsReports.NAME) }
        YouTubeAnalyticsReportsImpl(
            logger = withTag(YouTubeAnalyticsReports::class.simpleName!!),
            youtube = youtube,
            analytics = analytics,
        )
    }
}
