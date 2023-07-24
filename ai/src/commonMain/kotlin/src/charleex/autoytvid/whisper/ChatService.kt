package src.charleex.autoytvid.whisper

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import src.charleex.autoytvid.api.openapi.OpenAiApi
import src.charleex.autoytvid.whisper.model.ModelId
import src.charleex.autoytvid.whisper.model.chat.ChatCompletion
import src.charleex.autoytvid.whisper.model.chat.ChatCompletionChunk
import src.charleex.autoytvid.whisper.model.chat.ChatCompletionFunction
import src.charleex.autoytvid.whisper.model.chat.ChatCompletionRequest
import src.charleex.autoytvid.whisper.model.chat.ChatMessage
import src.charleex.autoytvid.whisper.model.chat.FunctionMode
import src.charleex.autoytvid.whisper.model.chat.extensions.streamEventsFrom
import src.charleex.autoytvid.whisper.model.chat.extensions.streamRequestOf

private const val API_PATH_CHAT_COMPLETIONS = "chat/completions"
private const val API_PATH_COMPLETIONS = "completions"

interface ChatService {
    suspend fun chatCompletion(
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
    ): ChatCompletion

    fun chatCompletions(
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

internal class ChatServiceImpl(
    private val requester: OpenAiApi,
    private val modelId: ModelId,
) : ChatService {
    override suspend fun chatCompletion(
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
    ): ChatCompletion {
        val request = ChatCompletionRequest(
            model = modelId,
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
        return requester.perform {
            it.post {
                url(path = API_PATH_CHAT_COMPLETIONS)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun chatCompletions(
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
        val request = ChatCompletionRequest(
            model = modelId,
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
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = API_PATH_CHAT_COMPLETIONS)
            setBody(streamRequestOf(request))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return flow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }
}
