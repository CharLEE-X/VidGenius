package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(
    @SerialName("id") val id: String,
    @SerialName("created") val created: Int,
    @SerialName("choices") val choices: List<ChatChoice>,
    @SerialName("usage") val usage: Usage? = null,
)
