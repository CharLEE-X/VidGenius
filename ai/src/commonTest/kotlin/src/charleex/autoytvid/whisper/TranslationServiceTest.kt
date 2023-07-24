package src.charleex.autoytvid.whisper

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

class TranslationServiceTestKoinTest : KoinTest {
    private lateinit var sut: TranslationService

    @BeforeTest
    fun setup() {
        stopKoin()
        startKoin {
            modules(whisperModule)
        }
        sut = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test polish audio translation should output correct text and language name`() =
        runTest(timeout = 60.seconds) {
            val expect = "Hi, how are you? What's the weather like today?"
            val actual = sut.translateAudio(
                "$commonResourcesPrefix/test-pl.wav",
                prompt = "Translate to english",
            )
            println("ACTUAL: $actual")

            assertEquals(expect, actual.text)
        }
}
