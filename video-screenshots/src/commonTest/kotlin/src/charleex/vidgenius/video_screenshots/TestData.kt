package src.charleex.vidgenius.video_screenshots

import java.io.File

object TestData {
    val videoFile: File = getResource("file/test-directory-with-mixed-files/video1.mp4")
    val notVideoFile: File = getResource("file/test-directory-with-mixed-files/not-video1.json")
    val directoryNoVideoFiles: File = getResource("file/test-directory-with-no-videos")
    val directoryWithMixedFiles: File = getResource("file/test-directory-with-mixed-files")

    private fun getResource(path: String) =
        this.javaClass.classLoader.getResource(path)?.file?.let(::File)!!
}
