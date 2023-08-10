package com.charleex.vidgenius.datasource.feature.open_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.VideoProcessingImpl
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.open_ai.model.ContentInfo
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionChunk
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionFunction
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatRole
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.FunctionMode
import com.charleex.vidgenius.datasource.feature.youtube.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

interface OpenAiRepository {
    suspend fun getDescriptionContext(video: Video, category: Category): Video
    suspend fun getMetaData(video: Video, category: Category): Video

    fun chats(
        messages: List<ChatMessage> = emptyList(),
        temperature: Double? = null,
        topP: Double? = null,
        n: Int? = null,
        stop: List<String>? = null,
        maxTokens: Int? = null,
        presencePenalty: Double? = null,
        frequencyPenalty: Double? = null,
        logitBias: Map<String, Int>? = null,
        user: String? = null,
        functions: List<ChatCompletionFunction>? = null,
        functionCall: FunctionMode? = null,
    ): Flow<ChatCompletionChunk>

    suspend fun getContentInfo(title: String, description: String?, tags: List<String>): ContentInfo
}

internal class OpenAiRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val chatService: ChatService,
) : OpenAiRepository {

    override suspend fun getDescriptionContext(video: Video, category: Category): Video {
        val descriptionsString = video.descriptions
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content =
                    "Here is a list of screenshot descriptions. Pick those related to the funny $category videos, and return " +
                            "them: $descriptionsString. If you cannot find an funny $category videos choose one the most popular one."
                ),
            )
        )

        logger.d("[$TAG] ANSWER: $chatCompletion")
        val context = chatCompletion.choices
            .mapNotNull { it.message?.content }
            .joinToString(", ")
        val newVideo = video.copy(descriptionContext = context)
        database.videoQueries.upsert(newVideo)
        return newVideo
    }

    override suspend fun getContentInfo(
        title: String,
        description: String?,
        tags: List<String>,
    ): ContentInfo {
        val languages = VideoProcessingImpl.languageCodes
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = """
                        Here is a title:
                        $title

                        and description: 
                        $description

                        and tags:
                        $tags

                        translate them to languages: $languages and return them as Json. Remove emojis from non english titles.
                        
                        {
                          "en-US": {
                            "title": "the title i gave you",
                            "description": "the description i gave you"
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
                            "first tag i passed",
                            "second tag i passed",
                            "third tag i passed",
                            "forth tag i passed",
                            "fifth tag i passed",
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
        val contentInfo = json.decodeFromString(ContentInfo.serializer(), contentInfoAsString)

        logger.i("ANSWER: $contentInfoAsString")

        return contentInfo
    }

    override suspend fun getMetaData(video: Video, category: Category): Video {
        val languages = VideoProcessingImpl.languageCodes
        val descriptions = video.descriptions
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = """
                        Here is a list of screenshot descriptions for $category videos for YouTube: $descriptions. 
                        If no descriptions then use the category $category.
                        Generate it in languages ${languages} and return it as json, don't add any extra text. 
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
        val contentInfo = json.decodeFromString(ContentInfo.serializer(), contentInfoAsString)

        logger.i("ANSWER: $contentInfoAsString")

        val newVideo = video.copy(contentInfo = contentInfo)
        database.videoQueries.upsert(newVideo)

        return newVideo
    }

    override fun chats(
        messages: List<ChatMessage>,
        temperature: Double?,
        topP: Double?,
        n: Int?,
        stop: List<String>?,
        maxTokens: Int?,
        presencePenalty: Double?,
        frequencyPenalty: Double?,
        logitBias: Map<String, Int>?,
        user: String?,
        functions: List<ChatCompletionFunction>?,
        functionCall: FunctionMode?,
    ): Flow<ChatCompletionChunk> {
        return chatService.chatCompletions(
            messages = messages,
            temperature = temperature,
            topP = topP,
            n = n,
            stop = stop,
            maxTokens = maxTokens,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias,
            user = user,
            functions = functions,
            functionCall = functionCall,
        )
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
