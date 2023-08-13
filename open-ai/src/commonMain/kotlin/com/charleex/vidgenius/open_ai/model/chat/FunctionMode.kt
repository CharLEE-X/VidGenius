package com.charleex.vidgenius.open_ai.model.chat

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
import com.charleex.vidgenius.open_ai.model.chat.FunctionMode.Companion.Auto
import com.charleex.vidgenius.open_ai.model.chat.FunctionMode.Companion.None
import com.charleex.vidgenius.open_ai.model.chat.FunctionMode.Default
import com.charleex.vidgenius.open_ai.model.chat.FunctionMode.Named

/**
 * This interface determines how the model handles function calls.
 *
 * There are several modes of operation:
 * - [Default]: In this mode, the model does not invoke any function [None] or decides itself [Auto] on calling a function or responding directly to the end-user. This mode becomes default if any functions are specified.
 * - [Named]: In this mode, the model will call a specific function, denoted by the `name` attribute.
 */
@Serializable(with = FunctionModeSerializer::class)
sealed interface FunctionMode {

    /**
     * Represents a function call mode.
     * The value can be any string representing a specific function call mode.
     */
    class Default(val value: String) : FunctionMode

    /**
     * Represents a named function call mode.
     * The name indicates a specific function that the model will call.
     *
     * @property name the name of the function to call.
     */
    @Serializable
    data class Named(val name: String) : FunctionMode

    /** Provides default function call modes. */
    companion object {
        /** Represents the `auto` mode. */
        val Auto: FunctionMode = Default("auto")

        /** Represents the `none` mode. */
        val None: FunctionMode = Default("none")
    }
}

internal object FunctionModeSerializer : KSerializer<FunctionMode> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    override fun deserialize(decoder: Decoder): FunctionMode {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionCall`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> Default(json.content)
            is JsonObject -> json["name"]?.jsonPrimitive?.content?.let(FunctionMode::Named)
                ?: error("Missing 'name'")

            else -> throw UnsupportedOperationException("Cannot deserialize FunctionMode. Unsupported JSON element.")
        }
    }

    override fun serialize(encoder: Encoder, value: FunctionMode) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionCall`" }
        when (value) {
            is Default -> encoder.encodeString(value.value)
            is Named -> Named.serializer().serialize(encoder, value)
        }
    }
}
