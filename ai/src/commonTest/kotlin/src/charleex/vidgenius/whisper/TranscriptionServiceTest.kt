package src.charleex.vidgenius.whisper

import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private const val commonResourcesPrefix = "src/commonTest/resources"

class TranscriptionServiceTest : KoinTest {
    private lateinit var sut: TranscriptionService

    @BeforeTest
    fun setup() {
        stopKoin()
        startKoin {
            modules(openAiModule)
        }
        sut = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test polish audio transcription should output correct text and language name`() =
        runTest(timeout = 60.seconds) {
            val expect = "Cześć! Jak się masz? Jaka jest pogoda dzisiaj?"
            val actual = sut.transcriptAudio(
                "$commonResourcesPrefix/test-pl.wav",
                prompt = "Translate to english",
            )
            println("ACTUAL: $actual")

            assertEquals(expect, actual.text)
            assertEquals("polish", actual.language)
        }
}
