package src.charleex.vidgenius.whisper

import kotlinx.coroutines.test.runTest
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
                        content = "Here is a list of screenshot descriptions. Pick those related to the animal, and return them: $descriptions"
                    ),
                )
            )
            println("ACTUAL: $actual")

            assertEquals(true, actual.choices.isNotEmpty())
        }
}
