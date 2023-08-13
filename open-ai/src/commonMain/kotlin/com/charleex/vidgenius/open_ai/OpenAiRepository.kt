package com.charleex.vidgenius.open_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.open_ai.model.ContentInfo
import com.charleex.vidgenius.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.open_ai.model.chat.ChatRole
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

interface OpenAiRepository {
    suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String

    suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo
}

internal class OpenAiRepositoryImpl(
    private val logger: Logger,
    private val chatService: ChatService,
) : OpenAiRepository {

    override suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String {
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content =
                    "Here is a list of screenshot descriptions. Pick those related to the funny $categoryQuery videos, and return " +
                            "them: $descriptions. If you cannot find an funny $categoryQuery videos choose one the most popular one."
                ),
            )
        )

        logger.d("ANSWER: $chatCompletion")
        val context = chatCompletion.choices
            .mapNotNull { it.message?.content }
            .joinToString(", ")
        logger.d { "context: $context" }
        return context
    }

    override suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo {
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = """
                        Here is a list of screenshot descriptions for $categoryQuery videos for YouTube: $descriptions. 
                        If no descriptions then use the category $categoryQuery.
                        Generate it in languages $languageCodes and return it as json, don't add any extra text. 
                        Make title to be catchy phrase and to have related emojis at the front and back of the title. 
                        Make description SEO friendly, 100 words long and include best hashtags at the front of description.  
                        Create 5 best ranking SEO tags.
                        
                        {
                          "en-US": {
                            "title": "generated title here",
                            "description": "generated description here"
                          },
                          "es": {
                            "title": "generated title here",
                            "description": "generated description here"
                          },
                          "zh": {
                            "title": "generated title here",
                            "description": "generated description here"
                          },
                          "pt": {
                            "title": "generated title here",
                            "description": "generated description here"
                          },
                          "hi": {
                            "title": "generated title here",
                            "description": "generated description here"
                          },
                          "tags": [
                            "generated tag with no hashtag",
                            "generated tag with no hashtag",
                            "generated tag with no hashtag",
                            "generated tag with no hashtag",
                            "generated tag with no hashtag",
                          ]
                        }

                        RETURN JSON OBJECT ONLY!!!
                    """.trimIndent()
                ),
            )
        )

        val message = chatCompletion.choices.firstOrNull()?.message ?: error("No message found")

        val contentInfoAsString = message.content
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        logger.i("ANSWER: $contentInfoAsString")
        return json.decodeFromString(ContentInfo.serializer(), contentInfoAsString)
    }
}

internal class OpenAiRepositoryDebug : OpenAiRepository {
    override suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String {
        delay(100)
        return "description context"
    }

    override suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo {
        delay(300)
        return ContentInfo()
    }
}
