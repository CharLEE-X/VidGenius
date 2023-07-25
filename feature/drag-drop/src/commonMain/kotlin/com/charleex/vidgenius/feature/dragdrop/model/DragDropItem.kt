package com.charleex.vidgenius.feature.dragdrop.model

import com.charleex.vidgenius.datasource.db.Video
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

data class DragDropItem(
    val id: String = UUID.randomUUID().toString(),
    val path: String,
    val duration: Long? = null,
    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = createdAt,
)

fun Video.toDragDropItem(): DragDropItem {
    return DragDropItem(
        id = this.id,
        path = this.path,
        duration = this.duration,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
    )
}

fun DragDropItem.video(): Video {
    return Video(
        id = this.id,
        path = this.path,
        duration = this.duration ?: 0,
        screenshots = emptyList(),
        title = null,
        description = null,
        tags = emptyList(),
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
    )
}
