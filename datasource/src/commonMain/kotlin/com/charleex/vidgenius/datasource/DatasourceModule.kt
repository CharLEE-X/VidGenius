package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Config
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.ConfigManagerImpl
import com.charleex.vidgenius.datasource.feature.local_video.videoFileModule
import com.charleex.vidgenius.datasource.feature.youtube.model.Category
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.charleex.vidgenius.datasource.feature.youtube.youtubeModule
import com.charleex.vidgenius.datasource.model.LocalVideo
import com.charleex.vidgenius.datasource.model.ProgressState
import com.charleex.vidgenius.datasource.model.YtVideo
import com.charleex.vidgenius.datasource.utils.DataTimeService
import com.charleex.vidgenius.datasource.utils.DateTimeServiceImpl
import com.charleex.vidgenius.datasource.utils.UuidProvider
import com.charleex.vidgenius.datasource.utils.UuidProviderImpl
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
import com.charleex.vidgenius.open_ai.openAiModule
import com.charleex.vidgenius.vision_ai.visionAiModule
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

fun datasourceModule(isDebugBuild: Boolean = getIsDebugBuild()) = module {
    val appDataDir = createAppDataDir()

    includes(
        databaseModule,
        platformModule(appDataDir),
        settingsModule,
        openAiModule(isDebugBuild),
        visionAiModule(isDebugBuild),
        youtubeModule(appDataDir, isDebugBuild),
        videoFileModule(appDataDir),
    )

    single<VideoProcessing> {
        VideoProcessingImpl(
            logger = Logger.withTag(VideoProcessing::class.simpleName!!),
            database = get(),
            localVideoProcessor = get(),
            openAiRepository = get(),
            googleCloudRepository = get(),
            youtubeRepository = get(),
            uuidProvider = get(),
            datetimeService = get(),
        )
    }

    single<VideoService> { params ->
        VideoServiceImpl(
            logger = Logger.withTag(VideoService::class.simpleName!!),
            videoProcessor = get(),
            youtubeRepository = get(),
            configManager = get(),
            scope = params.get(),
        )
    }

    single<ConfigManager> {
        ConfigManagerImpl(
            logger = Logger.withTag(ConfigManager::class.simpleName!!),
            database = get(),
            googleAuth = get(),
            scope = CoroutineScope(Dispatchers.Default),
        )
    }

    single<DataTimeService> {
        DateTimeServiceImpl()
    }
    single<UuidProvider> {
        UuidProviderImpl()
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
                ConfigAdapter = get(),
                VideoAdapter = get(),
            )
        }
        single {
            Config.Adapter(
                ytConfigAdapter = YtConfig.serializer().asColumnAdapter(),
                categoryAdapter = Category.serializer().asColumnAdapter(),
                selectedPrivacyStatusesAdapter = ListSerializer(PrivacyStatus.serializer()).asColumnAdapter(),
            )
        }
        single {
            Video.Adapter(
                ytVideoAdapter = YtVideo.serializer().asColumnAdapter(),
                localVideoAdapter = LocalVideo.serializer().asColumnAdapter(),
                progressStateAdapter = ProgressState.serializer().asColumnAdapter(),
                createdAtAdapter = InstantComponentSerializer.asColumnAdapter(),
                modifiedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
            )
        }
    }

private fun <T : Any> KSerializer<T>.asColumnAdapter(
    json: Json = Json {
        ignoreUnknownKeys = true
    },
) =
    JsonColumnAdapter(json, this)

private class JsonColumnAdapter<T : Any>(
    private val json: Json,
    private val serializer: KSerializer<T>,
) :
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
