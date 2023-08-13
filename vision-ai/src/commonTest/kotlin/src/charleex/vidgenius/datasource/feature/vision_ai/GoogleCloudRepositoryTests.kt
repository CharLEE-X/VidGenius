package src.charleex.vidgenius.datasource.feature.vision_ai

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.vision_ai.GoogleCloudRepository
import com.charleex.vidgenius.vision_ai.GoogleCloudRepositoryImpl
import com.charleex.vidgenius.vision_ai.VisionAiApi
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.given
import io.mockative.mock
import kotlinx.coroutines.test.runTest
import src.charleex.vidgenius.datasource.TestData
import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleCloudRepositoryTests {
    private lateinit var sut: GoogleCloudRepository

    @Mock
    private val visionAiApi = mock(classOf<VisionAiApi>())

    private fun startTest(
        getTextFromImage: Map<Float, String> = emptyMap(),
        block: suspend () -> Unit,
    ) = runTest {
        given(visionAiApi)
            .function(visionAiApi::fetchTextFromImage)
            .whenInvokedWith(any())
            .thenReturn(getTextFromImage)

        sut = GoogleCloudRepositoryImpl(
            logger = withTag("GoogleCloudRepositoryTests"),
            visionAiApi = visionAiApi,
        )

        block()
    }

    @Test
    fun `when passed LocalVideo then getTextFromImages should return descriptions for each screenshot`() =
        startTest(
            getTextFromImage = mapOf(
                0.9f to "description1",
                0.8f to "description2",
                0.7f to "description3",
            )
        ) {
            val actual = sut.getDescriptionsFromScreenshots(
                listOf(
                    TestData.image1.absolutePath,
                    TestData.image2.absolutePath,
                    TestData.image3.absolutePath,
                )
            )
            assertEquals(3, actual.size)
        }

    @Test
    fun `when got results then getTextFromImages should filter scores`() =
        startTest(
            getTextFromImage = mapOf(
                GoogleCloudRepositoryImpl.MIN_SCORE + 0.01f to "description1",
                GoogleCloudRepositoryImpl.MIN_SCORE to "description2",
                GoogleCloudRepositoryImpl.MIN_SCORE - 0.01f to "description3",
            )
        ) {
            val actual = sut.getDescriptionsFromScreenshots(
                listOf(
                    TestData.image1.absolutePath,
                    TestData.image2.absolutePath,
                    TestData.image3.absolutePath,
                )
            )
            assertEquals("description1 description2", actual.first())
            assertEquals("description1 description2", actual[1])
            assertEquals("description1 description2", actual.last())
        }
}

private val labelAnnotations = """
label_annotations {
  mid: "/m/0h9mv"
  description: "Tire"
  score: 0.9725592
  topicality: 0.9725592
}
label_annotations {
  mid: "/m/083wq"
  description: "Wheel"
  score: 0.9551757
  topicality: 0.9551757
}
label_annotations {
  mid: "/m/05s2s"
  description: "Plant"
  score: 0.9506865
  topicality: 0.9506865
}
label_annotations {
  mid: "/m/0k4j"
  description: "Car"
  score: 0.94499296
  topicality: 0.94499296
}
label_annotations {
  mid: "/m/07yv9"
  description: "Vehicle"
  score: 0.93995184
  topicality: 0.93995184
}
label_annotations {
  mid: "/m/0h8pb3l"
  description: "Automotive tire"
  score: 0.9229495
  topicality: 0.9229495
}
label_annotations {
  mid: "/m/0cblv"
  description: "Ecoregion"
  score: 0.9224916
  topicality: 0.9224916
}
label_annotations {
  mid: "/m/0466kx"
  description: "Off-road racing"
  score: 0.88293177
  topicality: 0.88293177
}
label_annotations {
  mid: "/m/0h8ls87"
  description: "Automotive exterior"
  score: 0.79384357
  topicality: 0.79384357
}
label_annotations {
  mid: "/m/0d74dx"
  description: "Fender"
  score: 0.78987783
  topicality: 0.78987783
}
"""
