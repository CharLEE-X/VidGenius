package src.charleex.vidgenius.api.openapi

import io.ktor.http.HttpHeaders
import src.charleex.vidgenius.api.client.ClientHost
import src.charleex.vidgenius.api.client.HttpClientConfig
import src.charleex.vidgenius.api.client.ProxyConfig
import src.charleex.vidgenius.api.client.RetryStrategy
import src.charleex.vidgenius.api.client.Timeout
import kotlin.time.Duration.Companion.seconds

private const val OPEN_AI_BASE_URL = "https://api.openai.com/v1/"
private const val OPENAI_API_KEY = "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"

internal class OpenAiConfig(
    override val token: String = OPENAI_API_KEY,
    override val timeout: Timeout = Timeout(socket = 60.seconds),
    override val organization: String? = null,
    override val headers: Map<String, String> = mapOf(
        HttpHeaders.ContentType to "application/json",
        HttpHeaders.Accept to "application/json",
        HttpHeaders.Authorization to "Bearer $OPENAI_API_KEY"
    ),
    override val host: ClientHost = ClientHost(baseUrl = OPEN_AI_BASE_URL),
    override val proxy: ProxyConfig? = null,
    override val retry: RetryStrategy = RetryStrategy(),
) : HttpClientConfig
