package src.charleex.autoytvid.processor.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.util.UUID

data class Video(
    val id: String = UUID.randomUUID().toString(),
    val file: File,
    val name: String = file.name,
    val path: String = file.path,
    val extension: String = file.extension,
    val videoType: VideoType,
    val timestamp: Instant = Clock.System.now(),
)

internal fun File.toVideo(): Video {
    return Video(
        file = this,
        videoType = this.videoType(),
    )
}
