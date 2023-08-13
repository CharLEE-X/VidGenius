package com.charleex.vidgenius.open_ai.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing a response from the chat completion api.
 *
 * [documentation](https://platform.openai.com/docs/api-reference/chat/create)
 */
@Serializable
data class ChatCompletion(
    /**
     * A unique id assigned to this completion
     */
    @SerialName("id") val id: String,
    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created") val created: Int,

    /**
     * The model used.
     */
    @SerialName("model") val model: String,

    /**
     * A list of generated completions
     */
    @SerialName("choices") val choices: List<ChatChoice>,

    /**
     * Text completion usage data.
     */
    @SerialName("usage") val usage: Usage? = null,
)
