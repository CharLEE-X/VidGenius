package src.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.feature.local_video.FileProcessor
import com.charleex.vidgenius.datasource.feature.local_video.LocalVideoProcessorImpl
import com.charleex.vidgenius.datasource.feature.local_video.LocalVideoProcessor
import com.charleex.vidgenius.datasource.feature.local_video.ScreenshotCapturing
import com.charleex.vidgenius.datasource.utils.DateTimeService
import com.charleex.vidgenius.datasource.utils.UuidProvider
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.given
import io.mockative.mock
import io.mockative.thenDoNothing
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import src.charleex.vidgenius.datasource.TestData
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

    @Mock
    private val datetimeService = mock(classOf<DateTimeService>())

    @Mock
    private val uuidProvider = mock(classOf<UuidProvider>())

    private fun startTest(
        filterVideoFiles: List<File> = emptyList(),
        captureScreenshot: File = File(""),
        getVideoDuration: Long = 0,
        nowInstant: Long = 0L,
        uuid: String = "123",
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

        given(datetimeService)
            .function(datetimeService::nowInstant)
            .whenInvoked()
            .thenReturn(Instant.fromEpochMilliseconds(nowInstant))

        given(uuidProvider)
            .function(uuidProvider::uuid)
            .whenInvoked()
            .thenReturn(uuid)

        sut = LocalVideoProcessorImpl(
            logger = withTag(this.javaClass.simpleName),
            fileProcessor = fileProcessor,
            screenshotCapturing = screenshotCapturing,
            datetimeService = datetimeService,
            uuidProvider = uuidProvider,
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
            assertEquals(file.absolutePath, result.first().path)
        }
    }

    @Test
    fun `when passed list with video then filterVideos should return correct LocalVideo`() {
        val fileName = "video1"
        val file = File(fileName)
        startTest(
            filterVideoFiles = listOf(file),
        ) {
            val result = sut.filterVideos(listOf(file))
            assertEquals("123", result.first().id)
            assertEquals(fileName, result.first().name)
            assertEquals(file.absolutePath, result.first().path)
            assertEquals(false, result.first().isCompleted)
            assertEquals(emptyList(), result.first().descriptions)
            assertEquals(emptyList(), result.first().screenshots)
            assertEquals(null, result.first().descriptionContext)
            assertEquals(null, result.first().contentInfo)
            assertEquals(Instant.fromEpochMilliseconds(0L), result.first().createdAt)
            assertEquals(Instant.fromEpochMilliseconds(0L), result.first().modifiedAt)
        }
    }

    @Test
    fun `when passed LocalVideo then captureScreenshots should return LocalVideo with captured screenshots`() {
        val localVideo = TestData.localVideo
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            val result = sut.captureScreenshots(localVideo, 3)
            assertTrue(result.screenshots.isNotEmpty())
        }
    }

    @Test
    fun `when number of screenshots then captureScreenshots should return correct number of screenshots screenshots`() {
        val localVideo = TestData.localVideo
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            val result = sut.captureScreenshots(localVideo, 3)
            assertEquals(3, result.screenshots.size)
        }
    }

    @Test
    fun `when passed LocalVideo file does not exist then captureScreenshots should throw an exception`() {
        val localVideo = TestData.localVideo.copy(
            path = "videoFile.absolutePath",
        )
        startTest(
            captureScreenshot = File("screenshot1"),
        ) {
            assertFailsWith<IllegalStateException> {
                sut.captureScreenshots(localVideo, 3)
            }
        }
    }
}
