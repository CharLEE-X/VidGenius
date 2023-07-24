package src.charleex.autoytvid.whisper.model.chat.extensions

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import src.charleex.autoytvid.api.client.JsonLenient

internal inline fun <reified T> streamRequestOf(serializable: T): JsonElement {
    val enableStream = "stream" to JsonPrimitive(true)
    val json = JsonLenient.encodeToJsonElement(serializable)
    val map = json.jsonObject.toMutableMap().also { it += enableStream }
    return JsonObject(map)
}
