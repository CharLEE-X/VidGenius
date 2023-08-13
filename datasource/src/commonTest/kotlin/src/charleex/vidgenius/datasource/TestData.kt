package src.charleex.vidgenius.datasource

import com.charleex.vidgenius.datasource.model.LocalVideo
import kotlinx.datetime.Instant
import java.io.File

object TestData {
    val videoFile: File = getResource("file/test-directory-with-mixed-files/video1.mp4")
    val notVideoFile: File = getResource("file/test-directory-with-mixed-files/not-video1.json")
    val directoryNoVideoFiles: File = getResource("file/test-directory-with-no-videos")
    val directoryWithMixedFiles: File = getResource("file/test-directory-with-mixed-files")

    val localVideo = LocalVideo(
        id = "123",
        name = videoFile.nameWithoutExtension,
        path = videoFile.absolutePath,
        isCompleted = false,
        descriptions = emptyList(),
        screenshots = emptyList(),
        descriptionContext = null,
        contentInfo = null,
        createdAt = Instant.fromEpochMilliseconds(0L),
        modifiedAt = Instant.fromEpochMilliseconds(0L),
    )

    private fun getResource(path: String) =
        this.javaClass.classLoader.getResource(path)?.file?.let(::File)!!
}
