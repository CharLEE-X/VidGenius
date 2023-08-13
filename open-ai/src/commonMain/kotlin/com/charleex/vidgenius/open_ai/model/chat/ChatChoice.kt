package com.charleex.vidgenius.open_ai.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatChoice(
    /**
     * Chat choice index.
     */
    @SerialName("index") val index: Int? = null,
    /**
     * The generated chat message.
     */
    @SerialName("message") val message: ChatMessage? = null,

    /**
     * The reason why OpenAI stopped generating.
     */
    @SerialName("finish_reason") val finishReason: String? = null,
)
