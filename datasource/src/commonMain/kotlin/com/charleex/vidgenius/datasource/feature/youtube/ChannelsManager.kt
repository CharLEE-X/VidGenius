package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Config
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.ChannelConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.ytChannels
import com.google.common.collect.Lists
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

interface ChannelsManager {
    val config: StateFlow<Config>
    fun getMyChannels(): List<ChannelConfig>
    suspend fun chooseChannel(newChannelConfig: ChannelConfig)
}

internal class ChannelsManagerImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val googleAuth: GoogleAuth,
    private val scope: CoroutineScope,
) : ChannelsManager {
    private val defaultConfig = Config(
        id = uuid4().toString(),
        channelConfig = null,
    )

    init {
        database.configQueries.getAll().executeAsList().ifEmpty {
            database.configQueries.upsert(defaultConfig)
        }
    }

    override val config: StateFlow<Config>
        get() = database.configQueries.getAll().asFlow()
            .map { it.executeAsOne() }
            .stateIn(scope, SharingStarted.WhileSubscribed(), defaultConfig)

    override fun getMyChannels(): List<ChannelConfig> {
        return ytChannels
    }

    override suspend fun chooseChannel(newChannelConfig: ChannelConfig) {
        logger.d("Choosing channel $newChannelConfig")
        withContext(Dispatchers.IO) {
            val config = database.configQueries.getAll().executeAsList().first()

            if (config.channelConfig?.id == newChannelConfig.id) {
                logger.d("Channel already chosen")
                return@withContext
            }


            config.channelConfig?.id?.let { previousChannelId ->
                logger.d("Signing out of channel $previousChannelId")
                googleAuth.signOut(previousChannelId)
            }

            logger.d("Deleting all videos")
            database.videoQueries.getAll().executeAsList().forEach {
                database.videoQueries.delete(it.id)
            }

            logger.d("Deleting all yt videos")
            database.ytVideoQueries.getAll().executeAsList().forEach {
                database.ytVideoQueries.delete(it.id)
            }

            logger.d("Signing in to channel ${newChannelConfig.title}")
            val scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube")
            val credentials = googleAuth.authorize(
                scopes = scopes,
                channelConfig = newChannelConfig,
            )

            logger.d("Credentials: $credentials")

            if (credentials.clientAuthentication == null) {
                logger.d("Failed to sign in to channel")
                return@withContext
            }

            logger.d("Signed in to channel ${newChannelConfig.title}")

            logger.d("Updating config with channel id ${newChannelConfig.id}")
            val newConfig = config.copy(channelConfig = newChannelConfig)
            database.configQueries.upsert(newConfig)
        }
    }
}

