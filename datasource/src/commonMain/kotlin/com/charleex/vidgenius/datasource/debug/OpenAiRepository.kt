package com.charleex.vidgenius.datasource.debug

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.AudioTranscription
import com.charleex.vidgenius.datasource.model.Message
import com.charleex.vidgenius.datasource.model.MetaData
import com.charleex.vidgenius.datasource.model.Role
import com.charleex.vidgenius.datasource.repository.OpenAiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okio.FileSystem
import src.charleex.vidgenius.whisper.model.ModelId
import src.charleex.vidgenius.whisper.model.chat.ChatCompletionChunk
import src.charleex.vidgenius.whisper.model.chat.ChatCompletionFunction
import src.charleex.vidgenius.whisper.model.chat.ChatMessage
import src.charleex.vidgenius.whisper.model.chat.FunctionMode

internal class OpenAiRepositoryDebug : OpenAiRepository {
    override suspend fun transcryptAudio(
        messageId: Int,
        filePath: String,
        fileSystem: FileSystem,
    ): AudioTranscription {
        return AudioTranscription(
            id = 1,
            originalText = "debug message",
            englishText = "debug first",
            language = "debug language",
            transcriptionTime = 0L,
            translationTime = 0L,
        )
    }

    override suspend fun uploadData(
        crashId: String,
        audioTranscription: AudioTranscription,
        latitude: Double,
        longitude: Double,
    ): Message {
        return Message(
            id = 1,
            message = "debug message",
            language = "debug conversationLanguage",
            role = Role.AI,
            answerTime = 0L,
        )
    }

    override suspend fun getDescriptionContext(descriptions: List<String>): String {
        delay(100)
        return "debug answer"
    }

    override suspend fun getMetaData(video: Video): MetaData {
        delay(300)
        return MetaData(
            title = "debug title",
            description = "debug description",
            tags = listOf("debug tag"),
        )
    }

    override suspend fun chat(
        messageId: Int,
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
    ): Message {
        delay(1000)
        return Message(
            id = messageId,
            message = "debug message",
            language = "debug conversationLanguage",
            role = Role.AI,
            answerTime = 0L,
        )
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
        return flowOf(
            ChatCompletionChunk(
                "debug message",
                1,
                model = ModelId(""),
                choices = listOf()
            )
        )
    }

    private suspend fun getTranscription(
        id: Int,
        filePath: String,
        fileSystem: FileSystem,
    ): Message {
        return Message(
            id = id,
            message = "debug transcription.first.text",
            language = "debug transcription.first.language",
            role = Role.USER,
            answerTime = 0L,
        )
    }

    private suspend fun getTranslation(
        filePath: String,
        fileSystem: FileSystem,
    ): String {
        return "translation.first.text"
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
