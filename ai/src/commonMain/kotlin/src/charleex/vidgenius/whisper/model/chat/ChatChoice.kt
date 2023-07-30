package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChatChoice(
    /**
     * Chat choice index.
     */
    @SerialName("index") public val index: Int? = null,
    /**
     * The generated chat message.
     */
    @SerialName("message") public val message: ChatMessage? = null,

    /**
     * The reason why OpenAI stopped generating.
     */
    @SerialName("finish_reason") public val finishReason: String? = null,
)
