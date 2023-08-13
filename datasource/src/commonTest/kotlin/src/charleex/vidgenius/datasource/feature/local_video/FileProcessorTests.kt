package src.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.feature.local_video.FileProcessor
import com.charleex.vidgenius.datasource.feature.local_video.FileProcessorImpl
import kotlinx.coroutines.test.runTest
import src.charleex.vidgenius.datasource.TestData
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileProcessorTests {
    private lateinit var sut: FileProcessor

    @BeforeTest
    fun setup() {
        sut = FileProcessorImpl(
            logger = withTag("FileProcessorTests"),
        )
    }

    @Test
    fun `when passed empty list then filterVideoFiles should return empty list`() =
        runTest {
            val expect = emptyList<File>()
            val actual = sut.filterVideoFiles(expect)
            assertEquals(expect, actual)
        }

    @Test
    fun `when passed list of video files then filterVideoFiles should return list of files`() =
        runTest {
            val expect = listOf(TestData.videoFile, TestData.videoFile)
            val actual = sut.filterVideoFiles(expect)
            assertEquals(expect, actual)
        }

    @Test
    fun `when passed mixed list of files then filterVideoFiles should return list of files`() =
        runTest {
            val given = listOf(TestData.videoFile, TestData.notVideoFile)
            val expect = listOf(TestData.videoFile)
            val actual = sut.filterVideoFiles(given)
            assertEquals(expect, actual)
        }

    @Test
    fun `when passed list of directories then filterVideoFiles should return list of files`() =
        runTest {
            val given = listOf(TestData.directoryWithMixedFiles, TestData.directoryNoVideoFiles)
            val expect = listOf(TestData.videoFile)
            val actual = sut.filterVideoFiles(given)
            assertEquals(expect, actual)
        }

    @Test
    fun `when passed list of files and directories then filterVideoFiles should return list of files`() =
        runTest {
            val given = listOf(TestData.videoFile, TestData.directoryWithMixedFiles, TestData.directoryNoVideoFiles)
            val expect = listOf(TestData.videoFile, TestData.videoFile)
            val actual = sut.filterVideoFiles(given)
            assertEquals(expect, actual)
        }

    @Test
    fun `when file throws an Exception should return list of files`() =
        runTest {
            val given = listOf(TestData.videoFile, File(""), TestData.directoryNoVideoFiles)
            val expect = listOf(TestData.videoFile)
            val actual = sut.filterVideoFiles(given)
            assertEquals(expect, actual)
        }

    @Test
    fun `when delete file then file should be deleted`() =
        runTest {
            val newFile1 = File.createTempFile("test", "test1")
            val newFile2 = File.createTempFile("test", "test2")
            sut.deleteFile(newFile1.absolutePath)
            assertTrue(newFile2.exists())
            assertFalse(newFile1.exists())
        }
}
