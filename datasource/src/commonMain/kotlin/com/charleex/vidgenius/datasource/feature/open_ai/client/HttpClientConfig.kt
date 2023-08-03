package com.charleex.vidgenius.datasource.feature.open_ai.client

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface HttpClientConfig {
    val token: String
    val timeout: Timeout
    val organization: String?
    val headers: Map<String, String>
    val host: ClientHost
    val proxy: ProxyConfig?
    val retry: RetryStrategy
}

class ClientHost(
    val baseUrl: String,
    val queryParams: Map<String, String> = emptyMap()
)

sealed interface ProxyConfig {
    class Http(val url: String) : ProxyConfig
    class Socks(val host: String, val port: Int) : ProxyConfig
}

class RetryStrategy(
    val maxRetries: Int = 3,
    val base: Double = 2.0,
    val maxDelay: Duration = 60.seconds,
)

class Timeout(
    val request: Duration? = null,
    val connect: Duration? = null,
    val socket: Duration? = null,
)
