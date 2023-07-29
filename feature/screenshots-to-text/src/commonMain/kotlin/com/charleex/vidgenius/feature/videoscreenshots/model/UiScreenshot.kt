package com.charleex.vidgenius.feature.videoscreenshots.model

import kotlinx.datetime.Instant
import com.charleex.vidgenius.datasource.model.Screenshot

data class UiScreenshot(
    val id: String,
    val path: String,
    val description: String? = null,
    val videoId: String,
    val createdAt: String,
    val modifiedAt: String,
)

internal fun Screenshot.toUiScreenshot() = UiScreenshot(
    id = this.id,
    path = this.path,
    description = this.description,
    videoId = this.videoId,
    createdAt = this.createdAt.toString(),
    modifiedAt = this.modifiedAt.toString(),
)

internal fun UiScreenshot.toScreenshot() = Screenshot(
    id = this.id,
    path = this.path,
    description = this.description,
    videoId = this.videoId,
    createdAt = Instant.parse(this.createdAt),
    modifiedAt = Instant.parse(this.createdAt),
)
