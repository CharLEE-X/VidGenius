package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import src.charleex.autoytvid.whisper.model.ModelId

@Serializable
internal class ChatCompletionRequest(
    @SerialName("model") val model: ModelId,
    @SerialName("messages") val messages: List<ChatMessage>,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("n") val n: Int? = null,
    @SerialName("stop") val stop: List<String>? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("logit_bias") val logitBias: Map<String, Int>? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("functions") val functions: List<ChatCompletionFunction>? = null,
    @SerialName("function_call") val functionCall: FunctionMode? = null,
)
