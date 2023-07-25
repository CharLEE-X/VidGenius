package src.charleex.vidgenius.processor.file

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.util.UUID

data class VideoFile(
    val id: String = UUID.randomUUID().toString(),
    val file: File,
    val name: String = file.name,
    val absolutePath: String = file.absolutePath,
    val extension: String = file.extension,
    val videoType: VideoType,
    val timestamp: Instant = Clock.System.now(),
)

internal fun File.toVideo(): VideoFile {
    return VideoFile(
        file = this,
        videoType = this.videoType(),
    )
}
