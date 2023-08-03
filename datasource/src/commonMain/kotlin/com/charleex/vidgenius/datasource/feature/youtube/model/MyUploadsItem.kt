package com.charleex.vidgenius.datasource.feature.youtube.model

import com.charleex.vidgenius.datasource.db.YtVideo
import kotlinx.datetime.Instant

data class MyUploadsItem(
    val ytId: String,
    val title: String,
    val description: String?,
    val tags: List<String>,
    val privacyStatus: String?,
    val publishedAt: Instant = Instant.DISTANT_PAST,
)

fun MyUploadsItem.toYtVideo(): YtVideo {
    return YtVideo(
        id = this.ytId,
        title = this.title,
        description = this.description,
        tags = this.tags,
        privacyStatus = this.privacyStatus,
        publishedAt = this.publishedAt,
    )
}
