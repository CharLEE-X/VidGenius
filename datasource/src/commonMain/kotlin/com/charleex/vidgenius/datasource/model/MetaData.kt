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
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class MetaData(
    val title: String,
    val description: String,
    val tags: List<String>,
)

object MetaDataSerializer : KSerializer<MetaData> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MetaData") {
            element<String>("TITLE")
            element<String>("DESC")
            element<List<String>>("TAGS")
        }

    override fun serialize(encoder: Encoder, value: MetaData) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeStringElement(descriptor, 0, value.title)
        compositeOutput.encodeStringElement(descriptor, 1, value.description)
        compositeOutput.encodeSerializableElement(descriptor, 2, ListSerializer(String.serializer()), value.tags)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): MetaData {
        val json = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val content = json.decodeJsonElement().jsonPrimitive.content
        val lines = content.split("\n")
        val title = lines.find { it.startsWith("TITLE:") }?.removePrefix("TITLE: ") ?: error("TITLE not found")
        val description = lines.find { it.startsWith("DESC:") }?.removePrefix("DESC: ") ?: error("DESC not found")
        val tagsLine = lines.find { it.startsWith("TAGS:") }?.removePrefix("TAGS: ")
        val tags = tagsLine?.split(", ") ?: emptyList()

        return MetaData(title, description, tags)
    }
}
