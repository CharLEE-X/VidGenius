package com.charleex.vidgenius.feature.process_video.model

import com.charleex.vidgenius.datasource.model.Screenshot
import kotlinx.datetime.Instant

data class UiScreenshot(
    val id: String,
    val videoId: String,
    val path: String,
    val description: String? = null,
    val createdAt: Instant,
    val modifiedAt: Instant
)

fun Screenshot.toUiScreenshot(): UiScreenshot {
    return UiScreenshot(
        id = this.id,
        videoId = this.videoId,
        path = this.path,
        description = this.description,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
    )
}

fun UiScreenshot.video(): Screenshot {
    return Screenshot(
        id = this.id,
        videoId = this.videoId,
        path = this.path,
        description = this.description,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
    )
}
