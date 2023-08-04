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
