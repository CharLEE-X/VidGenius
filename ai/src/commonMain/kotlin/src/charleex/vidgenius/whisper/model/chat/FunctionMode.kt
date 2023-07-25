package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = FunctionModeSerializer::class)
sealed interface FunctionMode {
    class Default(val value: String) : FunctionMode


    @Serializable
    data class Named(val name: String) : FunctionMode

    companion object {
        val Auto: FunctionMode = Default("auto")
        val None: FunctionMode = Default("none")
    }
}

internal object FunctionModeSerializer : KSerializer<FunctionMode> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    override fun deserialize(decoder: Decoder): FunctionMode {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionCall`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> FunctionMode.Default(json.content)
            is JsonObject -> json["name"]?.jsonPrimitive?.content?.let(FunctionMode::Named)
                ?: error("Missing 'name'")

            else -> throw UnsupportedOperationException("Cannot deserialize FunctionMode. Unsupported JSON element.")
        }
    }

    override fun serialize(encoder: Encoder, value: FunctionMode) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionCall`" }
        when (value) {
            is FunctionMode.Default -> encoder.encodeString(value.value)
            is FunctionMode.Named -> FunctionMode.Named.serializer().serialize(encoder, value)
        }
    }
}
