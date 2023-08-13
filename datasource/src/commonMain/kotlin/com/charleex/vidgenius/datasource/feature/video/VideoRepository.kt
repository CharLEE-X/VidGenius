package com.charleex.vidgenius.datasource.feature.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface VideoRepository {
    val videos: Flow<List<Video>>

    fun createVideo(video: Video): Video
    fun getVideo(id: String): Flow<Video>
    fun deleteVideo(id: String): Boolean
    fun updateVideo(video: Video): Video
}

internal class VideoRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
) : VideoRepository {
    override val videos: Flow<List<Video>>
        get() = database.videoQueries.getAll().asFlow().map { it.executeAsList() }

    override fun createVideo(video: Video): Video {
        logger.d { "Creating video: $video" }
        database.videoQueries.upsert(video)
        return database.videoQueries.getById(video.id).executeAsOne()
    }

    override fun getVideo(id: String): Flow<Video> {
        logger.d { "Getting video with id: $id" }
        return database.videoQueries.getById(id).asFlow().map { it.executeAsOne() }
    }

    override fun deleteVideo(id: String): Boolean {
        logger.d { "Deleting video with id: $id" }
        database.videoQueries.delete(id)
        val video = database.videoQueries.getById(id).executeAsOneOrNull()
        return video == null
    }

    override fun updateVideo(video: Video): Video {
        logger.d { "Updating video: $video" }
        database.videoQueries.upsert(video)
        return database.videoQueries.getById(video.id).executeAsOne()
    }
}
