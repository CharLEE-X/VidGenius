package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import src.charleex.vidgenius.whisper.model.ModelId

@Serializable
internal class CompletionRequest(
    @SerialName("model") val model: ModelId,
    @SerialName("prompt") val prompt: String? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("n") val n: Int? = null,
    @SerialName("logprobs") val logprobs: Int? = null,
    @SerialName("echo") val echo: Boolean? = null,
    @SerialName("stop") val stop: List<String>? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("best_of") val bestOf: Int? = null,
    @SerialName("logit_bias") val logitBias: Map<String, Int>? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("suffix") val suffix: String? = null,
)
