package src.charleex.vidgenius.whisper

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import src.charleex.vidgenius.whisper.model.chat.ChatChoice
import src.charleex.vidgenius.whisper.model.chat.ChatMessage
import src.charleex.vidgenius.whisper.model.chat.ChatRole
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ChatServiceTest : KoinTest {
    private lateinit var sut: ChatService

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
    fun `test sendig description gives metadata`() =
        runTest(timeout = 60.seconds) {
            val descriptions = "Floor, Dog, Canidae, Hardwood, Interior design"
            val expect: List<ChatChoice> = listOf()
            val actual = sut.chatCompletion(
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User.role,
                        content = "Here is a list of screenshot descriptions for animal funny videos:\n$descriptions\n\n" +
                                "Generate:\n" +
                                "- TITLE: for the YouTube video with related emojis at the front and back of the title.\n" +
                                "- DESC: SEO friendly\n" +
                                "- TAGS: 5 SEO friendly tags."
                    ),
                )
            )
            println("ACTUAL: $actual")

            assertEquals(true, actual.choices.isNotEmpty())
        }
}
