package src.charleex.vidgenius.datasource

import com.charleex.vidgenius.datasource.OpenAiRepository
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import com.charleex.vidgenius.datasource.di.repositoryModule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

const val commonResourcesPrefix = "src/commonTest/resources"

// /micro-machines.wav
// /multilingual.wav

class TranscriptionServiceTest : KoinTest {
    private lateinit var sut: OpenAiRepository

    @BeforeTest
    fun setup() {
        stopKoin()
        startKoin {
            modules(repositoryModule)
        }
        sut = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test english audio transcription should output correct text and language name`() =
        runTest(timeout = 60.seconds) {
            val answer = sut.uploadData(
                crashId = "crashId",
            )
            println("ANSWER: $answer")

            assertEquals("Hola", answer.message)
        }
}
