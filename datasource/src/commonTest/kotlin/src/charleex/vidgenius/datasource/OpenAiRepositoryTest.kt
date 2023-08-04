package src.charleex.vidgenius.datasource

import org.hamcrest.MatcherAssert.assertThat
import org.koin.test.KoinTest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenAiRepositoryTest : KoinTest {
//    private lateinit var sut: OpenAiRepository

//    @BeforeTest
//    fun setup() {
//        stopKoin()
//        startKoin {
//            modules(datasourceModule)
//        }
//        sut = get()
//    }
//
//    @AfterTest
//    fun tearDown() {
//        stopKoin()
//    }

    @Test
    fun `test english audio transcription should output correct text and language name`() {

        val inputString =
            "TITLE: \uD83D\uDC36\uD83E\uDD23 Hilarious Animal Moments Caught on Camera \uD83D\uDC31\uD83D\uDC37\uD83D\uDC30 Funniest Pet Fails! \uD83D\uDC3E\uD83C\uDFA5\\nDESCRIPTION: Get ready to ROFL with this compilation of the funniest animal fails! From mischievous cats to silly dogs and adorable bunnies, these hilarious moments will make your day brighter. Don't miss out on the laughter, watch now!\\nTAGS: funny animals, animal fails, hilarious pets, cute animal videos, funny pet moments"
        val lines = inputString.split("\\n")

        val title = lines
            .find { it.startsWith("TITLE:") }
            ?.removePrefix("TITLE: ")
            ?: error("Title not found")

        val description = lines
            .find { it.startsWith("DESCRIPTION:") }
            ?.removePrefix("DESCRIPTION: ")
            ?: error("Description not found")

        val tagsLine = lines
            .find { it.startsWith("TAGS:") }
            ?.removePrefix("TAGS: ")
            ?: error("Tags not found")

        val tags = tagsLine
            .split(", ")
            .map { it.trim() }
            .map { tag ->
                tag
                    .split(" ")
                    .map { tagList ->
                        tagList.replaceFirstChar {
                            if (it.isLowerCase())
                                it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    }
            }
            .map { it.joinToString("") }

        val hashtags = tags.joinToString(" ") { "#$it" }

        println(
            """
                
            1. Title:
               $title
         
            2. Description:
               $hashtags
               $description
            
            3. Tags:
               $tags
            
        """.trimIndent()
        )
    }

    @Test
    fun `split string on new lines regex`() {
        val inputString =
            "TITLE: 🐶🐱 Hilarious Animal Antics 😂🦆 Funny Video Compilation 🐼🐸\n\nDESCRIPTION: Get ready to laugh out loud with this side-splitting compilation of animal funny videos! Watch as mischievous cats, playful dogs, and quirky critters deliver endless entertainment. From a clever raccoon outsmarting its owner to a clumsy panda failing at tree climbing, these hilarious moments are sure to brighten your day. Don't forget to like, share, and subscribe for more hysterical animal antics! 🐾🤣✨\n\nTAGS: animal funny videos, hilarious animal antics, funny video compilation, cute animals, laughter-inducing moments"
        val lines = inputString.split("\n\n")

        val title = lines
            .find { it.startsWith("TITLE:") }
            ?.removePrefix("TITLE: ")
            ?: error("Title not found")

        val description = lines
            .find { it.startsWith("DESCRIPTION:") }
            ?.removePrefix("DESCRIPTION: ")
            ?: error("Description not found")

        val tagsLine = lines
            .find { it.startsWith("TAGS:") }
            ?.removePrefix("TAGS: ")
            ?: error("Tags not found")

        val tags = tagsLine
            .split(", ")
            .map { it.trim() }
            .map { tag ->
                tag
                    .split(" ")
                    .map { tagList ->
                        tagList.replaceFirstChar {
                            if (it.isLowerCase())
                                it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    }
            }
            .map { it.joinToString("") }

        val hashtags = tags.joinToString(" ") { "#$it" }

        println(
            """
                
            1. Title:
               $title
         
            2. Description:
               $hashtags
               $description
            
            3. Tags:
               $tags
            
        """.trimIndent()
        )
    }

    @Test
    fun `find title line`() {
        val info =
            "TITLE: \uD83D\uDC36\uD83E\uDD23 Hilarious Animal Moments Caught on Camera \uD83D\uDC31\uD83D\uDC37\uD83D\uDC30 Funniest Pet Fails! \uD83D\uDC3E\uD83C\uDFA5\\nDESCRIPTION: Get ready to ROFL with this compilation of the funniest animal fails! From mischievous cats to silly dogs and adorable bunnies, these hilarious moments will make your day brighter. Don't miss out on the laughter, watch now!\\nTAGS: funny animals, animal fails, hilarious pets, cute animal videos, funny pet moments"
        val split = info.split("\\n")

        val actual: String? = split
            .shuffled()
            .find { it.startsWith("TITLE:") }
        val expect =
            "TITLE: \uD83D\uDC36\uD83E\uDD23 Hilarious Animal Moments Caught on Camera \uD83D\uDC31\uD83D\uDC37\uD83D\uDC30 Funniest Pet Fails! \uD83D\uDC3E\uD83C\uDFA5"

        println(
            """
                EXPECT:
                $expect
                ACTUAL:
                $actual
            """.trimIndent()
        )
        assertEquals(expect, actual)
    }

    @Test
    fun `trim title from string`() {
        val info =
            "TITLE: \uD83D\uDC36\uD83E\uDD23 Hilarious Animal Moments Caught on Camera \uD83D\uDC31\uD83D\uDC37\uD83D\uDC30 Funniest Pet Fails! \uD83D\uDC3E\uD83C\uDFA5\\nDESCRIPTION: Get ready to ROFL with this compilation of the funniest animal fails! From mischievous cats to silly dogs and adorable bunnies, these hilarious moments will make your day brighter. Don't miss out on the laughter, watch now!\\nTAGS: funny animals, animal fails, hilarious pets, cute animal videos, funny pet moments"
        val split = info.split("\\n")
        val title = split
            .shuffled()
            .find { it.contains("TITLE:") }

        val actual: String = title!!
            .removePrefix("TITLE: ")
        val expect =
            "\uD83D\uDC36\uD83E\uDD23 Hilarious Animal Moments Caught on Camera \uD83D\uDC31\uD83D\uDC37\uD83D\uDC30 Funniest Pet Fails! \uD83D\uDC3E\uD83C\uDFA5"

        println(
            """
                EXPECT:
                $expect
                ACTUAL:
                $actual
            """.trimIndent()
        )
        assertEquals(expect, actual)
    }
}
