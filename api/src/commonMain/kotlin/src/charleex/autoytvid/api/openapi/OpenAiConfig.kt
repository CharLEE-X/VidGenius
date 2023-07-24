package src.charleex.autoytvid.api.openapi

import src.charleex.autoytvid.api.client.HttpClientConfig
import src.charleex.autoytvid.api.client.ClientHost
import src.charleex.autoytvid.api.client.ProxyConfig
import src.charleex.autoytvid.api.client.RetryStrategy
import src.charleex.autoytvid.api.client.Timeout
import kotlin.time.Duration.Companion.seconds

private const val OPEN_AI_BASE_URL = "https://api.openai.com/v1/"
private const val OPENAI_API_KEY = "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"

internal class OpenAiConfig(
    override val token: String = OPENAI_API_KEY,
    override val timeout: Timeout = Timeout(socket = 60.seconds),
    override val organization: String? = null,
    override val headers: Map<String, String> = emptyMap(),
    override val host: ClientHost = ClientHost(baseUrl = OPEN_AI_BASE_URL),
    override val proxy: ProxyConfig? = null,
    override val retry: RetryStrategy = RetryStrategy(),
): HttpClientConfig
