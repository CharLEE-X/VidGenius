package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.db.Config
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.open_ai.model.ContentInfo
import com.charleex.vidgenius.datasource.feature.open_ai.openAiModule
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepository
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepositoryImpl
import com.charleex.vidgenius.datasource.feature.video_file.videoFileModule
import com.charleex.vidgenius.datasource.feature.vision_ai.visionAiModule
import com.charleex.vidgenius.datasource.feature.youtube.model.ChannelConfig
import com.charleex.vidgenius.datasource.feature.youtube.youtubeModule
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

fun datasourceModule() = module {
    val appDataDir = createAppDataDir()

    includes(
        databaseModule,
        platformModule(appDataDir),
        settingsModule,
        openAiModule,
        visionAiModule,
        youtubeModule(appDataDir),
        videoFileModule(appDataDir),
    )


    single<VideoFileRepository> {
        VideoFileRepositoryImpl(
            logger = withTag(VideoFileRepository::class.simpleName!!),
            fileProcessor = get(),
            screenshotCapturing = get(),
            database = get(),
        )
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
                ConfigAdapter = get(),
            )
        }
        single {
            Video.Adapter(
                screenshotsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                descriptionsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                createdAtAdapter = InstantComponentSerializer.asColumnAdapter(),
                modifiedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
                contentInfoAdapter = ContentInfo.serializer().asColumnAdapter(),
            )
        }
        single {
            YtVideo.Adapter(
                tagsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
                publishedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
            )
        }
        single {
            Config.Adapter(
                channelConfigAdapter = ChannelConfig.serializer().asColumnAdapter(),
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
