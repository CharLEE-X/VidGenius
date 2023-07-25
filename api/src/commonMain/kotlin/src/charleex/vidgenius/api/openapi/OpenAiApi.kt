package src.charleex.vidgenius.api.openapi

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.core.Closeable

interface OpenAiApi : Closeable {
    suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T

    suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T,
    )
}

internal class OpenAiApiImpl(private val httpClient: HttpClient) : OpenAiApi {
    override suspend fun <T : Any> perform(
        info: TypeInfo,
        block: suspend (HttpClient) -> HttpResponse,
    ): T {
        val response = block(httpClient)
        return response.body(info)
    }

    override suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T,
    ) {
        try {
            HttpStatement(builder = builder, client = httpClient).execute(block)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun close() {
        httpClient.close()
    }
}
