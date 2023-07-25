package com.charleex.vidgenius.feature.videodetail

import kotlinx.datetime.Instant
import src.charleex.vidgenius.repository.UploadItem

data class VideoDetail(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val publishedAt: Instant = Instant.DISTANT_PAST,
)

internal fun UploadItem.toVideoDetail(): VideoDetail {
    return VideoDetail(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
    )
}
