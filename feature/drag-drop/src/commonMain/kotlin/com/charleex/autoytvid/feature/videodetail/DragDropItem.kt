package com.charleex.autoytvid.feature.videodetail

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import src.charleex.autoytvid.processor.model.Video
import src.charleex.autoytvid.processor.model.VideoType
import java.io.File
import java.util.UUID

data class DragDropItem(
    val id: String = UUID.randomUUID().toString(),
    val file: File,
    val name: String = file.name,
    val path: String = file.path,
    val extension: String = file.extension,
    val videoType: VideoType,
    val timestamp: Instant = Clock.System.now(),
)

fun List<Video>.toDragDropItems(): List<DragDropItem> {
    return map { it.toDragDropItem() }
}

fun Video.toDragDropItem(): DragDropItem {
    return DragDropItem(
        id = this.id,
        file = this.file,
        name = this.name,
        path = this.path,
        extension = this.extension,
        videoType = this.videoType,
        timestamp = this.timestamp,
    )
}
