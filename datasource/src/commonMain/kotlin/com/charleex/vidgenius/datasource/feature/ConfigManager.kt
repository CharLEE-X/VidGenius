package com.charleex.vidgenius.datasource.feature

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Config
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.Category
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.allCategories
import com.charleex.vidgenius.datasource.feature.youtube.model.ytConfigs
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

interface ConfigManager {
    val config: StateFlow<Config>
    fun getMyChannels(): List<YtConfig>
    suspend fun setYtConfig(newYtConfig: YtConfig): Boolean
    suspend fun setCategory(category: Category)
    fun setPrivacyStatus(privacyStatus: PrivacyStatus)
}

internal class ConfigManagerImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val googleAuth: GoogleAuth,
    private val scope: CoroutineScope,
) : ConfigManager {
    private val defaultConfig = Config(
        id = uuid4().toString(),
        ytConfig = null,
        category = allCategories.first(),
        selectedPrivacyStatuses = listOf(PrivacyStatus.PRIVATE),
    )

    init {
        database.configQueries.getAll().executeAsList().ifEmpty {
            database.configQueries.upsert(defaultConfig)
        }
    }

    override val config: StateFlow<Config>
        get() = database.configQueries.getAll().asFlow()
            .map { it.executeAsOne() }
            .stateIn(scope, SharingStarted.WhileSubscribed(), getConfig())

    override fun getMyChannels(): List<YtConfig> {
        return ytConfigs
    }

    override suspend fun setYtConfig(newYtConfig: YtConfig): Boolean {
        logger.d("Choosing channel $newYtConfig")
        val config = getConfig()

        if (config.ytConfig?.id == newYtConfig.id) {
            logger.d("Channel already chosen")
            return true
        }


        config.ytConfig?.id?.let { previousChannelId ->
            logger.d("Signing out of channel $previousChannelId")
            googleAuth.signOut(previousChannelId)
        }

        logger.d("Signing in to channel ${newYtConfig.title}")
        val credentials = googleAuth.authorizeYouTube(newYtConfig.secretsFile)

        logger.d("Credentials: $credentials")

        if (credentials?.clientAuthentication == null) {
            logger.d("Failed to sign in to channel")
            return false
        }

        withContext(Dispatchers.IO) {
            logger.d("Deleting all videos")
            database.videoQueries.getAll().executeAsList().forEach {
                database.videoQueries.delete(it.id)
            }

            logger.d("Deleting all yt videos")
//            database.ytVideoQueries.getAll().executeAsList().forEach {
//                database.ytVideoQueries.delete(it.id)
//            }
        }

        logger.d("Signed in to channel ${newYtConfig.title}")

        logger.d("Updating config with channel id ${newYtConfig.id}")
        val newConfig = config.copy(ytConfig = newYtConfig)
        updateConfig(newConfig)
        return true
    }

    override suspend fun setCategory(category: Category) {
        logger.d("Choosing category $category")
        val currentConfig = getConfig()
        val updatedConfig = currentConfig.copy(category = category)
        updateConfig(updatedConfig)
    }

    override fun setPrivacyStatus(privacyStatus: PrivacyStatus) {
        logger.d("Choosing privacy status $privacyStatus")
        val selectedPrivacyStatuses = config.value.selectedPrivacyStatuses.toMutableList()
        if (privacyStatus in selectedPrivacyStatuses) {
            selectedPrivacyStatuses.remove(privacyStatus)
            logger.d("Removing privacy status $privacyStatus")
        } else {
            selectedPrivacyStatuses.add(privacyStatus)
            logger.d("Adding privacy status $privacyStatus")
        }
        logger.d("Selected privacy statuses: $selectedPrivacyStatuses")
        val newYtConfig = config.value.copy(selectedPrivacyStatuses = selectedPrivacyStatuses)
        database.configQueries.upsert(newYtConfig)
    }

    private fun getConfig(): Config {
        return database.configQueries.getAll().executeAsOne()
    }

    private suspend fun updateConfig(config: Config) {
        withContext(Dispatchers.IO) {
            database.configQueries.upsert(config)
        }
    }
}

