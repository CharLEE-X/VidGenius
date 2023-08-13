package src.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.feature.local_video.ScreenshotCapturing
import com.charleex.vidgenius.datasource.feature.local_video.ScreenshotCapturingImpl
import org.junit.Before
import src.charleex.vidgenius.datasource.TestData
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenshotCapturingTests {
    private lateinit var sut: ScreenshotCapturing

    private val tempDir = createTempDirectory("ScreenshotCapturingTests_")

    @Before
    fun setUp() {
        sut = ScreenshotCapturingImpl(
            logger = withTag("ScreenshotCapturingTests"),
            appDataDirFile = tempDir.toFile(),
        )
    }

    @AfterTest
    fun tearDown() {
        tempDir.toFile().deleteRecursively()
    }

    @Test
    fun `when provided file then captureScreenshot should return screenshot`() {
        val videoFile = TestData.videoFile
        val actual = sut.captureScreenshot(videoFile, 1L, 0)
        assertEquals("video1_1.png", actual.name)
    }

    @Test
    fun `when provided index then captureScreenshot should return file name with index plus 1`() {
        val videoFile = TestData.videoFile
        val actual = sut.captureScreenshot(videoFile, 1L, 1)
        assertEquals("video1_2.png", actual.name)
    }

    @Test
    fun `when captureScreenshot then file should be created in the appDataDirFile`() {
        val videoFile = TestData.videoFile
        val actual = sut.captureScreenshot(videoFile, 1L, 1)
        assertTrue(actual.absolutePath.contains(tempDir.pathString))
    }

    @Test
    fun `when provided file then getVideoDuration should return correct duration`() {
        val videoFile = TestData.videoFile
        val actual = sut.getVideoDuration(videoFile)
        assertEquals(66733, actual)
    }
}
