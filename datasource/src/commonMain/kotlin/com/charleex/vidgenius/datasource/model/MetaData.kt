package com.charleex.vidgenius.datasource.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
internal data class MetaData(
    val title: String,
    val description: String,
    val tags: List<String>,
)

internal object MetaDataSerializer : KSerializer<MetaData> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MetaData") {
            element<String>("title")
            element<String>("description")
            element<List<String>>("tags")
        }

    override fun serialize(encoder: Encoder, value: MetaData) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeStringElement(descriptor, 0, value.title)
        compositeOutput.encodeStringElement(descriptor, 1, value.description)
        compositeOutput.encodeSerializableElement(descriptor, 2, ListSerializer(String.serializer()), value.tags)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): MetaData {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val jsonElement = jsonDecoder.decodeJsonElement()

        if (jsonElement is JsonObject) {
            val title = jsonElement["title"]?.jsonPrimitive?.content ?: error("Title not found")
            val description = jsonElement["description"]?.jsonPrimitive?.content ?: error("Description not found")

            val tagsElement = jsonElement["tags"]?.jsonArray ?: error("Tags not found")
            val tags = tagsElement.map { it.jsonPrimitive.content }

            return MetaData(title, description, tags)
        } else {
            error("Invalid JSON format for MetaData")
        }
    }
}
