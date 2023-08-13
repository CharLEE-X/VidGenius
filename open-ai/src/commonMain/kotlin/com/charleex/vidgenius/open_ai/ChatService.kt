package com.charleex.vidgenius.open_ai

import com.charleex.vidgenius.open_ai.api.OpenAiApi
import com.charleex.vidgenius.open_ai.model.ModelId
import com.charleex.vidgenius.open_ai.model.chat.ChatCompletion
import com.charleex.vidgenius.open_ai.model.chat.ChatCompletionChunk
import com.charleex.vidgenius.open_ai.model.chat.ChatCompletionFunction
import com.charleex.vidgenius.open_ai.model.chat.ChatCompletionRequest
import com.charleex.vidgenius.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.open_ai.model.chat.FunctionMode
import com.charleex.vidgenius.open_ai.model.chat.extensions.streamEventsFrom
import com.charleex.vidgenius.open_ai.model.chat.extensions.streamRequestOf
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
            model = modelId.id,
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
                contentType(ContentType.Application.Json)
                setBody(request)
                headers {
                    append(HttpHeaders.Authorization, "no-cache")
                }
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
            model = modelId.id,
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
            contentType(ContentType.Application.Json)
            setBody(streamRequestOf(request))
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

internal suspend inline fun <reified T> OpenAiApi.perform(noinline block: suspend (HttpClient) -> HttpResponse): T {
    return perform(typeInfo<T>(), block)
}
