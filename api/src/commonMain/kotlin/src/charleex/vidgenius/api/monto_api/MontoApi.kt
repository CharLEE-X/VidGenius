package src.charleex.vidgenius.api.monto_api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import src.charleex.vidgenius.api.model.CrashMessage
import src.charleex.vidgenius.api.model.CrashMessageResponse

interface MontoApi {
    suspend fun sendCrashMessage(
        crashId: String,
        messageId: Int,
        originalText: String,
        englishText: String,
        language: String,
        latitude: Double,
        longitude: Double,
    ): CrashMessageResponse
}

internal class MontoApiImpl(
    private val httpClient: HttpClient,
) : MontoApi {
    override suspend fun sendCrashMessage(
        crashId: String,
        messageId: Int,
        originalText: String,
        englishText: String,
        language: String,
        latitude: Double,
        longitude: Double,
    ): CrashMessageResponse {
        val crashMessage = CrashMessage(
            originalText = originalText,
            englishText = englishText,
            language = language,
            latitude = latitude,
            longitude = longitude,
        )
//        Log.d(TAG, "sendCrashMessage: $crashMessage")
        val body = try {
            httpClient.post("/production/crash-message/${crashId}/${messageId}") {
                setBody(crashMessage)
            }.body<CrashMessageResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            CrashMessageResponse(
                message = "Monto | Server error.",
            )
        }
        return body
    }

    companion object {
        private const val TAG = "MontoApi"
    }
}
