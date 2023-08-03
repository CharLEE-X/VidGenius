package com.charleex.vidgenius.feature.videolist

import com.charleex.vidgenius.datasource.model.YtUploadItem
import kotlinx.datetime.Instant

data class VideoListItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val publishedAt: Instant,
)

internal fun List<YtUploadItem>.toVideoListItems(): List<VideoListItem> = map {
    VideoListItem(
        id = it.id,
        title = it.title,
        description = it.description,
        publishedAt = it.publishedAt,
    )
}
