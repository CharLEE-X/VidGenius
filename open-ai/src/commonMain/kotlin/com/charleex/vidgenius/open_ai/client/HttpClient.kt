package com.charleex.vidgenius.open_ai.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit

internal fun createHttpClient(config: HttpClientConfig) = HttpClient {
    engine {
        config.proxy?.let { proxyConfig ->
            proxy = when (proxyConfig) {
                is ProxyConfig.Http -> ProxyBuilder.http(proxyConfig.url)
                is ProxyConfig.Socks -> ProxyBuilder.socks(proxyConfig.host, proxyConfig.port)
            }
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, KotlinxSerializationConverter(JsonLenient))
    }

    install(Logging) {
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                println(message)
            }
        }
        level = LogLevel.ALL
    }

    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(accessToken = config.token, refreshToken = "")
            }
        }
    }

    install(HttpTimeout) {
        config.timeout.socket?.let { socketTimeout ->
            socketTimeoutMillis = socketTimeout.toLong(DurationUnit.MILLISECONDS)
        }
        config.timeout.connect?.let { connectTimeout ->
            connectTimeoutMillis = connectTimeout.toLong(DurationUnit.MILLISECONDS)
        }
        config.timeout.request?.let { requestTimeout ->
            requestTimeoutMillis = requestTimeout.toLong(DurationUnit.MILLISECONDS)
        }
    }

    install(HttpRequestRetry) {
        maxRetries = config.retry.maxRetries
        // retry on rate limit error.
        retryIf { _, response -> response.status.value.let { it == 429 } }
        exponentialDelay(config.retry.base, config.retry.maxDelay.inWholeMilliseconds)
    }

    defaultRequest {
        url(config.host.baseUrl)
        config.host.queryParams.onEach { (key, value) ->
            url.parameters.appendIfNameAbsent(key, value)
        }
        config.organization?.let { organization ->
            headers.append("OpenAI-Organization", organization)
        }
        config.headers.onEach { (key, value) ->
            headers.appendIfNameAbsent(key, value)
        }
    }

    expectSuccess = true
}

val JsonLenient = Json {
    isLenient = true
    ignoreUnknownKeys = true
    prettyPrint = true
}
