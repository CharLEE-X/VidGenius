package src.charleex.vidgenius.video_screenshots

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.feature.local_video.FileProcessor
import com.charleex.vidgenius.datasource.feature.local_video.LocalVideoProcessor
import com.charleex.vidgenius.datasource.feature.local_video.LocalVideoProcessorImpl
import com.charleex.vidgenius.datasource.feature.local_video.ScreenshotCapturing
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.given
import io.mockative.mock
import io.mockative.thenDoNothing
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LocalVideoProcessorTests {
    private lateinit var sut: LocalVideoProcessor

    @Mock
    private val screenshotCapturing = mock(classOf<ScreenshotCapturing>())

    @Mock
    private val fileProcessor = mock(classOf<FileProcessor>())

    private fun startTest(
        filterVideoFiles: List<File> = emptyList(),
        captureScreenshot: File = File(""),
        getVideoDuration: Long = 0,
        block: suspend () -> Unit,
    ) = runTest {
        given(fileProcessor)
            .function(fileProcessor::filterVideoFiles)
            .whenInvokedWith(any())
            .thenReturn(filterVideoFiles)
        given(fileProcessor)
            .function(fileProcessor::deleteFile)
            .whenInvokedWith(any())
            .thenDoNothing()

        given(screenshotCapturing)
            .function(screenshotCapturing::captureScreenshot)
            .whenInvokedWith(any(), any(), any())
            .thenReturn(captureScreenshot)

        given(screenshotCapturing)
            .function(screenshotCapturing::getVideoDuration)
            .whenInvokedWith(any())
            .thenReturn(getVideoDuration)

        sut = LocalVideoProcessorImpl(
            logger = withTag(this.javaClass.simpleName),
            fileProcessor = fileProcessor,
            screenshotCapturing = screenshotCapturing,
        )

        block()
    }

    @Test
    fun `when passed empty list then filterVideos should return empty`() = startTest {
        val result = sut.filterVideos(emptyList<File>())
        assert(result.isEmpty())
    }

    @Test
    fun `when passed list with video then filterVideos should return LocalVideo`() {
        val fileName = "video1"
        startTest(
            filterVideoFiles = listOf(File(fileName)),
        ) {
            val result = sut.filterVideos(listOf(File(fileName)))
            assertEquals(1, result.size)
        }
    }

    @Test
    fun `when passed list with video then filterVideos should return LocalVideo with path of the file absolutePath`() {
        val fileName = "video1"
        val file = File(fileName)
        startTest(
            filterVideoFiles = listOf(file),
        ) {
            val result = sut.filterVideos(listOf(file))
            assertEquals(file.absolutePath, result.first().absolutePath)
        }
    }

    @Test
    fun `when passed list with video then filterVideos should return correct video Files`() {
        val fileName = "video1"
        val file = File(fileName)
        startTest(
            filterVideoFiles = listOf(file),
        ) {
            val result = sut.filterVideos(listOf(file))
            assertEquals(file.absolutePath, result.map { it.absolutePath }.first())
        }
    }

    @Test
    fun `when passed LocalVideo then captureScreenshots should return LocalVideo with captured screenshots`() {
        val videoFilePath = TestData.videoFile.absolutePath
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            val results = sut.captureScreenshots(videoFilePath, 3)
            assertTrue(results.isNotEmpty())
        }
    }

    @Test
    fun `when number of screenshots then captureScreenshots should return correct number of screenshots screenshots`() {
        val videoFilePath = TestData.videoFile.absolutePath
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            val results = sut.captureScreenshots(videoFilePath, 3)
            assertEquals(3, results.size)
        }
    }

    @Test
    fun `when passed LocalVideo file does not exist then captureScreenshots should throw an exception`() {
        val videoFilePath = "videoFile.absolutePath"
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            assertFailsWith<IllegalStateException> {
                sut.captureScreenshots(videoFilePath, 3)
            }
        }
    }
}
