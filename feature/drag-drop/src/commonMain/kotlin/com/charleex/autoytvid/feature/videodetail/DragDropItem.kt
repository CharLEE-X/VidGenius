package com.charleex.autoytvid.feature.videodetail

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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

enum class VideoType {
    MP4,
    MKV,
    AVI,
    UNKNOWN;
}
