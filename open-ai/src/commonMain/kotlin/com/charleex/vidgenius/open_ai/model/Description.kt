package com.charleex.vidgenius.open_ai.model

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val short: String,
    val long: String,
    val subscribe: String,
    val tags: String,
)
