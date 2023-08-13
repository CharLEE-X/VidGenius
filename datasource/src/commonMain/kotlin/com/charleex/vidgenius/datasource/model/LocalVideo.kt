package com.charleex.vidgenius.datasource.model

import com.charleex.vidgenius.open_ai.model.ContentInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LocalVideo(
    val id: String,
    val name: String,
    val path: String,
    val screenshots: List<String>,
    val descriptions: List<String>,
    val descriptionContext: String?,
    val contentInfo: ContentInfo?,
    val isCompleted: Boolean,
    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
)
