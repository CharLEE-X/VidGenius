package com.charleex.vidgenius.datasource.model

import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class YtVideo(
    val id: String,
    val title: String,
    val description: String,
    val privacyStatus: PrivacyStatus,
    val tags: List<String>,
    val thumbnailUrl: String,
    val publishedAt: Instant,
)
