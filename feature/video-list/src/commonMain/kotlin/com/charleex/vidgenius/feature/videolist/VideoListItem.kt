package com.charleex.vidgenius.feature.videolist

import kotlinx.datetime.Instant
import com.charleex.vidgenius.datasource.UploadItem

data class VideoListItem(
    val id: String,
    val title: String,
    val description: String,
    val publishedAt: Instant,
)

internal fun List<UploadItem>.toVideoListItems(): List<VideoListItem> = map {
    VideoListItem(
        id = it.id,
        title = it.title,
        description = it.description,
        publishedAt = it.publishedAt,
    )
}
