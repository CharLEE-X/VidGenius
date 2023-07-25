package src.charleex.vidgenius.api.model

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrashMessage(
    @SerialName("originalText") val originalText: String,
    @SerialName("englishText") val englishText: String,
    @SerialName("language") val language: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("timestamp") val timestamp: String = Clock.System.now().toEpochMilliseconds().toString(),
)
