package com.charleex.autoytvid.feature.dragdrop

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import src.charleex.autoytvid.processor.file.VideoFile
import src.charleex.autoytvid.processor.file.VideoType
import java.awt.image.BufferedImage
import java.io.File
import java.util.UUID

data class DragDropItem(
    val id: String = UUID.randomUUID().toString(),
    val file: File,
    val name: String = file.name,
    val absolutePath: String = file.absolutePath,
    val extension: String = file.extension,
    val videoType: VideoType,
    val timestamp: Instant = Clock.System.now(),
    val screenshots: List<BufferedImage> = emptyList(),
    val isProcessing: Boolean = false,
)

fun List<VideoFile>.toDragDropItems(): List<DragDropItem> {
    return map { it.toDragDropItem() }
}

fun VideoFile.toDragDropItem(): DragDropItem {
    return DragDropItem(
        id = this.id,
        file = this.file,
        name = this.name,
        absolutePath = this.absolutePath,
        extension = this.extension,
        videoType = this.videoType,
        timestamp = this.timestamp,
    )
}

fun DragDropItem.toVideoFile(): VideoFile {
    return VideoFile(
        id = this.id,
        file = this.file,
        name = this.name,
        absolutePath = this.absolutePath,
        extension = this.extension,
        videoType = this.videoType,
        timestamp = this.timestamp,
    )
}
