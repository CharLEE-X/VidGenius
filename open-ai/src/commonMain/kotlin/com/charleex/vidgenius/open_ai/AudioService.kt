package com.charleex.vidgenius.open_ai

import com.charleex.vidgenius.open_ai.api.OpenAiApi
import com.charleex.vidgenius.open_ai.model.transcription.FileSource
import com.charleex.vidgenius.open_ai.model.transcription.ResponseFormat
import com.charleex.vidgenius.open_ai.model.transcription.Transcription
import com.charleex.vidgenius.open_ai.model.transcription.TranscriptionRequest
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.ContentType
import io.ktor.utils.io.core.writeFully
import okio.buffer
import okio.use

private const val API_PATH_TRANSLATION = "audio/translations"
private const val API_PATH_TRANSCRIPTION = "audio/transcriptions"

internal interface AudioService {
    suspend fun transcription(request: TranscriptionRequest): Transcription
}

internal class AudioServiceImpl(private val requester: OpenAiApi) : AudioService {
    override suspend fun transcription(request: TranscriptionRequest): Transcription {
        return when (request.responseFormat) {
            ResponseFormat.JSON.name.lowercase(),
            ResponseFormat.VERBOSE_JSON.name.lowercase(),
            null,
            -> transcriptionAsJson(request)

            else -> transcriptionAsString(request)
        }
    }

    private suspend fun transcriptionAsJson(request: TranscriptionRequest): Transcription {
        return try {
            requester.perform {
                it.submitFormWithBinaryData(
                    url = API_PATH_TRANSCRIPTION,
                    formData = formDataOf(request)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Transcription(e.localizedMessage ?: "transcriptionAsJson error")
        }
    }

    private suspend fun transcriptionAsString(request: TranscriptionRequest): Transcription {
        return try {
            val text = requester.perform<String> {
                it.submitFormWithBinaryData(
                    url = API_PATH_TRANSCRIPTION,
                    formData = formDataOf(request)
                )
            }
            Transcription(text)
        } catch (e: Exception) {
            e.printStackTrace()
            Transcription(e.localizedMessage ?: "transcriptionAsString error")
        }
    }

    private fun formDataOf(request: TranscriptionRequest, format: String? = null) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        val responseFormat = format ?: request.responseFormat
        responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
        request.language?.let { append(key = "language", value = it) }
    }
}

private fun FormBuilder.appendFileSource(key: String, fileSource: FileSource) {
    append(key, fileSource.name, ContentType.Application.OctetStream) {
        fileSource.source.buffer().use { source ->
            val buffer = ByteArray(8192) // 8 KiB
            var bytesRead: Int
            while (source.read(buffer).also { bytesRead = it } != -1) {
                writeFully(src = buffer, offset = 0, length = bytesRead)
            }
        }
    }
}

