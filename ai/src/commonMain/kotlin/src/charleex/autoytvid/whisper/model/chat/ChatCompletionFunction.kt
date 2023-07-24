package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionFunction(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parameters") val parameters: Parameters? = null,
)

class ChatCompletionFunctionBuilder {
    var name: String? = null
    var description: String? = null
    var parameters: Parameters? = null

    fun build(): ChatCompletionFunction = ChatCompletionFunction(
        name = requireNotNull(name) { "name is required" },
        description = description,
        parameters = parameters
    )
}

fun chatCompletionFunction(block: ChatCompletionFunctionBuilder.() -> Unit): ChatCompletionFunction =
    ChatCompletionFunctionBuilder().apply(block).build()
