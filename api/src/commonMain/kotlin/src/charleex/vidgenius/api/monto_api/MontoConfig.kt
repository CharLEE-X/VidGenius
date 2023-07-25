package src.charleex.vidgenius.api.monto_api

import io.ktor.http.HttpHeaders
import src.charleex.vidgenius.api.client.ClientHost
import src.charleex.vidgenius.api.client.HttpClientConfig
import src.charleex.vidgenius.api.client.ProxyConfig
import src.charleex.vidgenius.api.client.RetryStrategy
import src.charleex.vidgenius.api.client.Timeout
import kotlin.time.Duration.Companion.seconds

private const val BASE_URL = "https://2cji2wycc5.execute-api.eu-west-1.amazonaws.com/"
private const val API_KEY = "hackathonWinner2023"
private const val X_API_KEY_ = "x-api-key"
private const val X_API_KEY_VALUE = "eENzfCsWkl7TBH2I2EeZj5hJ36RNTOwrLSEotau5"

private val defaultHeaders = mapOf(
    HttpHeaders.ContentType to "application/json",
    HttpHeaders.Accept to "application/json",
    HttpHeaders.Authorization to API_KEY,
    X_API_KEY_ to X_API_KEY_VALUE,
)

internal class MontoConfig(
    override val token: String = API_KEY,
    override val timeout: Timeout = Timeout(socket = 60.seconds),
    override val organization: String? = null,
    override val headers: Map<String, String> = defaultHeaders,
    override val host: ClientHost = ClientHost(baseUrl = BASE_URL),
    override val proxy: ProxyConfig? = null,
    override val retry: RetryStrategy = RetryStrategy(),
) : HttpClientConfig
