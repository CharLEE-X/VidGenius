package com.charleex.vidgenius.feature.videodetail

import com.charleex.vidgenius.datasource.model.YtUploadItem
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class VideoDetail(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val publishedAt: Instant = Clock.System.now(),
)

internal fun YtUploadItem.toVideoDetail(): VideoDetail {
    return VideoDetail(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
    )
}
