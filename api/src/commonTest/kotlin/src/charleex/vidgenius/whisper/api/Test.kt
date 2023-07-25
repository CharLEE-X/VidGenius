package src.charleex.vidgenius.whisper.api

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TranscriptionServiceTest {
//    private lateinit var sut: TranscriptionService

    @BeforeTest
    fun setup() {
//        sut = TranscriptionServiceImpl(audioService, ModelId("whisper-1"))
    }

    @Test
    fun `test`() = runTest {
//        val expect =
//            "This is the Micromachine Man presenting the most midget miniature motorcade of micromachines. Each one has dramatic details, terrific trim, precision paint jobs, plus incredible micromachine pocket playsets. There's a police station, fire station, restaurant, service station, and more. Perfect pocket portables to take anyplace. And there are many miniature playsets to play with and each one comes with its own special edition micromachine vehicle and fun fantastic features that miraculously move. Raise the boat lift at the airport, marina, man the gun turret at the army base, clean your car at the car wash, raise the toll bridge. And these playsets fit together to form a micromachine world. Micromachine pocket playsets, so tremendously tiny, so perfectly precise, so dazzlingly detailed, you'll want to pocket them all. Micromachines are micromachine pocket playsets sold separately from Galoob. The smaller they are, the better they are."
//
//        val actual = sut.create("$commonResourcesPrefix/micro-machines.wav")
//            .also { println("ACTUAL: $it") }
//
//        assertEquals(expect, actual.text)
//        assertEquals("english", actual.language)
    }
}
