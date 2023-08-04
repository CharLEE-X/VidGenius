package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Config
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.YtChannel
import com.charleex.vidgenius.datasource.feature.youtube.model.ytChannels
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsServiceImpl
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
    fun getMyChannels(): List<YtChannel>
    suspend fun chooseChannel(ytChannel: YtChannel)
}

internal class ChannelsManagerImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val googleAuth: GoogleAuth,
    private val scope: CoroutineScope,
) : ChannelsManager {
    private val defaultConfig = Config(
        id = uuid4().toString(),
        channelId = null,
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

    override fun getMyChannels(): List<YtChannel> {
        return ytChannels
    }

    override suspend fun chooseChannel(ytChannel: YtChannel) {
        logger.d("Choosing channel $ytChannel")
        withContext(Dispatchers.IO) {
            val config = database.configQueries.getAll().executeAsList().first()
            val previousChannelId = config.channelId

            if (config.channelId == ytChannel.id) {
                logger.d("Channel already chosen")
                return@withContext
            }

            logger.d("Deleting all videos")
            database.videoQueries.getAll().executeAsList().forEach {
                database.videoQueries.delete(it.id)
            }

            logger.d("Deleting all yt videos")
            database.ytVideoQueries.getAll().executeAsList().forEach {
                database.ytVideoQueries.delete(it.id)
            }

            previousChannelId?.let {
                logger.d("Signing out of channel $previousChannelId")
                googleAuth.signOut(it)
            }

            logger.d("Signing in to channel ${ytChannel.title}")
            val credentials = googleAuth.authorize(
                scopes = MyUploadsServiceImpl.scopes,
                ytChannel = ytChannel,
            )

            logger.d("Credentials: $credentials")

            if (credentials.clientAuthentication == null) {
                logger.d("Failed to sign in to channel")
                return@withContext
            }

            logger.d("Signed in to channel ${ytChannel.title}")

            logger.d("Updating config with channel id ${ytChannel.id}")
            val newConfig = config.copy(channelId = ytChannel.id)
            database.configQueries.upsert(newConfig)
        }
    }
}

