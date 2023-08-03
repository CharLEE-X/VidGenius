package com.charleex.vidgenius.datasource.feature.open_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionChunk
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionFunction
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatRole
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.FunctionMode
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.flow.Flow

interface OpenAiRepository {
    suspend fun getDescriptionContext(video: Video): Video
    suspend fun getMetaData(video: Video): Video

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
}

internal class OpenAiRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val chatService: ChatService,
) : OpenAiRepository {

    override suspend fun getDescriptionContext(video: Video): Video {
        val descriptionsString = video.descriptions.joinToString(" ")
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = "Here is a list of screenshot descriptions. Pick those related to the animal, and return them: $descriptionsString. If you cannot find an animal return 'funny dog'"
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

    override suspend fun getMetaData(video: Video): Video {
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = "Here is a list of screenshot descriptions for animal funny videos:\n${video.descriptions.joinToString { ", " }}\n\n" +
                            "Generate:\n" +
                            "- TITLE: for the YouTube video with related emojis at the front and back of the title.\n" +
                            "- DESCRIPTION: SEO friendly\n" +
                            "- TAGS: 5 best ranking SEO tags."
                ),
            )
        )
        val message = chatCompletion.choices.firstOrNull()?.message ?: error("No message found")

        val inputString = message.content
        val lines = inputString.lines()
        val title = lines.find { it.startsWith("TITLE:", true) }?.removePrefix("TITLE: ")
            ?: error("Title not found")
        val description =
            lines.find { it.startsWith("DESCRIPTION:", true) }?.removePrefix("DESCRIPTION: ")
                ?: error("Description not found")
        val tagsLine = lines.find { it.startsWith("TAGS:", true) }?.removePrefix("TAGS: ") ?: error(
            "Tags not found"
        )
        val tags = tagsLine.split(", ").map { it.trim() }

        val newVideo = video.copy(
            title = title,
            description = description,
            tags = tags,
        )
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
