package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.open_ai.ChatService
import com.charleex.vidgenius.datasource.feature.open_ai.ChatServiceImpl
import com.charleex.vidgenius.datasource.feature.open_ai.OpenAiRepository
import com.charleex.vidgenius.datasource.feature.open_ai.OpenAiRepositoryDebug
import com.charleex.vidgenius.datasource.feature.open_ai.OpenAiRepositoryImpl
import com.charleex.vidgenius.datasource.feature.open_ai.api.OpenAiApi
import com.charleex.vidgenius.datasource.feature.open_ai.api.OpenAiApiImpl
import com.charleex.vidgenius.datasource.feature.open_ai.api.OpenAiConfig
import com.charleex.vidgenius.datasource.feature.open_ai.client.createHttpClient
import com.charleex.vidgenius.datasource.feature.open_ai.model.ModelId
import com.charleex.vidgenius.datasource.feature.video_file.ScreenshotCapturing
import com.charleex.vidgenius.datasource.feature.video_file.ScreenshotCapturingImpl
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepository
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepositoryImpl
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessor
import com.charleex.vidgenius.datasource.feature.video_file.model.FileProcessorImpl
import com.charleex.vidgenius.datasource.feature.vision_ai.GoogleCloudRepository
import com.charleex.vidgenius.datasource.feature.vision_ai.GoogleCloudRepositoryDebug
import com.charleex.vidgenius.datasource.feature.vision_ai.GoogleCloudRepositoryImpl
import com.charleex.vidgenius.datasource.feature.vision_ai.VisionAiService
import com.charleex.vidgenius.datasource.feature.vision_ai.VisionAiServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepositoryDebug
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepositoryImpl
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
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

private const val CREDENTIALS_DIRECTORY = ".oauth-credentials"

fun datasourceModule() = module {
    val appDataDir = createAppDataDir()

    includes(
        databaseModule,
        platformModule(appDataDir),
        settingsModule,
    )


    single<VideoFileRepository> {
        VideoFileRepositoryImpl(
            logger = withTag(VideoFileRepository::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
            database = get(),
        )
    }

    // Vision
    single<VisionAiService> {
        VisionAiServiceImpl(
            logger = withTag(VisionAiService::class.simpleName!!),
        )
    }
    single<GoogleCloudRepository> {
        if (getIsDebugBuild()) GoogleCloudRepositoryDebug().also { println("GoogleCloudRepository in DEBUG mode") }
        else GoogleCloudRepositoryImpl(
            logger = withTag(GoogleCloudRepository::class.simpleName!!),
            database = get(),
            visionAiService = get(),
        ).also { println("GoogleCloudRepository in RELEASE mode") }
    }

    // YouTube
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
    single<MyUploadsService> {
        MyUploadsServiceImpl(
            logger = withTag(MyUploadsService::class.simpleName!!),
            googleAuth = get(),
            httpTransport = get(),
            jsonFactory = get(),
        )
    }
    single<UploadVideoService> {
        UploadVideoServiceImpl(
            logger = withTag(UploadVideoService::class.simpleName!!),
            googleAuth = get(),
            httpTransport = get(),
            jsonFactory = get(),
        )
    }
    single<UpdateVideoService> {
        UpdateVideoServiceImpl(
            logger = withTag(UpdateVideoService::class.simpleName!!),
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
            logger = withTag(YoutubeRepository::class.simpleName!!),
            database = get(),
            googleAuth = get(),
            myUploadsService = get(),
            updateVideoService = get(),
        ).also { println("YoutubeRepository in RELEASE mode") }
    }

    // Video file
    single<FileProcessor> {
        FileProcessorImpl(
            logger = withTag(FileProcessor::class.simpleName!!),
        )
    }
    single<ScreenshotCapturing> {
        ScreenshotCapturingImpl(
            logger = withTag(ScreenshotCapturing::class.simpleName!!),
            appDataDir = appDataDir,
        )
    }
    single<VideoProcessing> {
        VideoProcessingImpl(
            logger = withTag(VideoProcessing::class.simpleName!!),
            database = get(),
            videoFileRepository = get(),
            openAiRepository = get(),
            googleCloudRepository = get(),
            youtubeRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }

    // Open ai
    val openAiConfig = OpenAiConfig()

    single<OpenAiApi> {
        OpenAiApiImpl(
            httpClient = createHttpClient(openAiConfig)
        )
    }
    single<ChatService> {
        ChatServiceImpl(
            requester = get(),
            modelId = ModelId("gpt-3.5-turbo"),
        )
    }
    single<OpenAiRepository> {
        if (getIsDebugBuild()) OpenAiRepositoryDebug().also { println("OpenAiRepository in DEBUG mode") }
        else OpenAiRepositoryImpl(
            logger = withTag(OpenAiRepository::class.simpleName!!),
            database = get(),
            chatService = get(),
        ).also { println("OpenAiRepository in RELEASE mode") }
    }
}

internal expect fun platformModule(appDataDir: File): Module

private const val USER_DATA_PREFS = "VidGeniusUserDataPreferences"

private val settingsModule
    get() = module {
        single<Preferences> {
            Preferences.userRoot().node(USER_DATA_PREFS)
        }
        single<Settings> {
            val delegate: Preferences = get<Preferences>()
            PreferencesSettings(delegate)
        }
        single<ObservableSettings> {
            val delegate: Preferences = get<Preferences>()
            PreferencesSettings(delegate)
        }
    }

private val databaseModule
    get() = module {
        single {
            VidGeniusDatabase(
                driver = get(),
                VideoAdapter = get(),
                YtVideoAdapter = get(),
            )
        }
        single {
            Video.Adapter(
                screenshotsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                descriptionsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                createdAtAdapter = InstantComponentSerializer.asColumnAdapter(),
                modifiedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
                tagsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
            )
        }
        single {
            YtVideo.Adapter(
                tagsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                publishedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
            )
        }
    }

private fun <T : Any> KSerializer<T>.asColumnAdapter(json: Json = Json { ignoreUnknownKeys = true }) =
    JsonColumnAdapter(json, this)

private class JsonColumnAdapter<T : Any>(private val json: Json, private val serializer: KSerializer<T>) :
    ColumnAdapter<T, String> {
    override fun decode(databaseValue: String): T = json.decodeFromString(serializer, databaseValue)
    override fun encode(value: T): String = json.encodeToString(serializer, value)
}

private fun createAppDataDir(): File {
    val userHomeDir = System.getProperty("user.home")
    val appDataDir = File(userHomeDir, "VidGeniusAppData")
    if (!appDataDir.exists()) {
        appDataDir.mkdir()
    }
    return appDataDir
}
