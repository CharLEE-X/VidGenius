package com.charleex.vidgenius.datasource.repository

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.AudioTranscription
import com.charleex.vidgenius.datasource.model.Message
import com.charleex.vidgenius.datasource.model.MetaData
import com.charleex.vidgenius.datasource.model.Role
import com.charleex.vidgenius.datasource.utils.measureTimeMillisPair
import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import src.charleex.vidgenius.api.monto_api.MontoApi
import src.charleex.vidgenius.whisper.ChatService
import src.charleex.vidgenius.whisper.TranscriptionService
import src.charleex.vidgenius.whisper.TranslationService
import src.charleex.vidgenius.whisper.model.chat.ChatCompletionChunk
import src.charleex.vidgenius.whisper.model.chat.ChatCompletionFunction
import src.charleex.vidgenius.whisper.model.chat.ChatMessage
import src.charleex.vidgenius.whisper.model.chat.ChatRole
import src.charleex.vidgenius.whisper.model.chat.FunctionMode

interface OpenAiRepository {
    suspend fun getDescriptionContext(descriptions: List<String>): String
    suspend fun getMetaData(video: Video): MetaData

    suspend fun chat(
        messageId: Int,
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
    ): Message

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

    suspend fun transcryptAudio(
        messageId: Int,
        filePath: String,
        fileSystem: FileSystem = FileSystem.SYSTEM,
    ): AudioTranscription

    /**
     * Flow:
     * - transcrypt - audio-to-text - output original text and language name
     * - store language name, to be used by next transcription
     * - translate - original text to english text - output english text
     * - send CrashMessage to the api
     * - receive an answer in the original language from the api
     * - return the 'Answer' with 'message'
     */
    suspend fun uploadData(
        crashId: String,
        audioTranscription: AudioTranscription,
        latitude: Double,
        longitude: Double,
    ): Message
}

internal class OpenAiRepositoryImpl(
    private val logger: Logger,
    private val montoApi: MontoApi,
    private val transcriptionService: TranscriptionService,
    private val translationService: TranslationService,
    private val chatService: ChatService,
) : OpenAiRepository {
    private var conversationLanguage: String? = null

    override suspend fun transcryptAudio(
        messageId: Int,
        filePath: String,
        fileSystem: FileSystem,
    ): AudioTranscription {
        val transcription = measureTimeMillisPair {
            getTranscription(messageId, filePath, fileSystem)
        }

        conversationLanguage = transcription.first.language

        val translation = measureTimeMillisPair {
            getTranslation(filePath, fileSystem)
        }

        return AudioTranscription(
            id = transcription.first.id + 1,
            originalText = transcription.first.message,
            englishText = translation.first,
            language = transcription.first.language,
            transcriptionTime = transcription.second,
            translationTime = translation.second,
        )
    }

    override suspend fun uploadData(
        crashId: String,
        audioTranscription: AudioTranscription,
        latitude: Double,
        longitude: Double,
    ): Message {
        val crashMessageResponse = measureTimeMillisPair {
            montoApi.sendCrashMessage(
                crashId = crashId,
                messageId = audioTranscription.id,
                originalText = audioTranscription.originalText,
                englishText = audioTranscription.englishText,
                language = audioTranscription.language ?: "english",
                latitude = latitude,
                longitude = longitude,
            )
        }
        logger.d("[$TAG] ANSWER: $crashMessageResponse, TIME: ${crashMessageResponse.second}")
        return Message(
            id = audioTranscription.id + 1,
            message = crashMessageResponse.first.message,
            language = conversationLanguage,
            role = Role.AI,
            answerTime = crashMessageResponse.second,
        )
    }

    override suspend fun getDescriptionContext(descriptions: List<String>): String {
        val descriptionsString = descriptions.joinToString(" ")
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = "Here is a list of screenshot descriptions. Pick those related to the animal, and return them: $descriptionsString"
                ),
            )
        )

        logger.d("[$TAG] ANSWER: $chatCompletion")
        return chatCompletion.choices
            .mapNotNull { it.message?.content }
            .joinToString(", ")
    }

    override suspend fun getMetaData(video: Video): MetaData {
        val chatCompletion = chatService.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User.role,
                    content = "Here is a list of screenshot descriptions for animal funny videos:\n${video.descriptionContext}\n\n" +
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
        val title = lines.find { it.startsWith("TITLE:", true) }?.removePrefix("Title: ") ?: error("Title not found")
        val description = lines.find { it.startsWith("DESCRIPTION:", true) }?.removePrefix("DESCRIPTION: ")
            ?: error("Description not found")
        val tagsLine = lines.find { it.startsWith("TAGS:", true) }?.removePrefix("TAGS: ") ?: error("Tags not found")
        val tags = tagsLine.split(", ").map { it.trim() }

        return MetaData(title, description, tags)
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
        val completion = measureTimeMillisPair {
            chatService.chatCompletion(
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
        logger.d("[$TAG] COMPLETION: $completion, TIME: ${completion.second}")
        return Message(
            id = messageId,
            message = completion.first.choices.map { it.message }.joinToString(separator = "\n"),
            language = conversationLanguage,
            role = Role.AI,
            answerTime = completion.second,
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

    private suspend fun getTranscription(
        id: Int,
        filePath: String,
        fileSystem: FileSystem,
    ): Message {
        val transcription = measureTimeMillisPair {
            transcriptionService.transcriptAudio(
                filePath = filePath,
                fileSystem = fileSystem,
                language = conversationLanguage?.codeISO6391(),
            )
        }
        logger.d("[$TAG] TRANSCRIPTION: $transcription, TIME: ${transcription.second}")
        conversationLanguage = transcription.first.language
        return Message(
            id = id,
            message = transcription.first.text,
            language = transcription.first.language,
            role = Role.USER,
            answerTime = transcription.second,
        )
    }

    private suspend fun getTranslation(
        filePath: String,
        fileSystem: FileSystem,
    ): String {
        val translation = measureTimeMillisPair {
            translationService.translateAudio(filePath, fileSystem)
        }
        println("[$TAG] TRANSLATION: $translation, TIME: ${translation.second}")
        return translation.first.text
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}

private fun String.codeISO6391() = when (this.lowercase()) {
    "english" -> "en"
    "spanish" -> "es"
    "french" -> "fr"
    "german" -> "de"
    "italian" -> "it"
    "japanese" -> "ja"
    "korean" -> "ko"
    "dutch" -> "nl"
    "portuguese" -> "pt"
    "russian" -> "ru"
    "chinese" -> "zh"
    "danish" -> "da"
    "finnish" -> "fi"
    "greek" -> "el"
    "hindi" -> "hi"
    "hungarian" -> "hu"
    "indonesian" -> "id"
    "norwegian" -> "no"
    "polish" -> "pl"
    "swedish" -> "sv"
    "thai" -> "th"
    "turkish" -> "tr"
    "czech" -> "cs"
    "arabic" -> "ar"
    else -> null
}
