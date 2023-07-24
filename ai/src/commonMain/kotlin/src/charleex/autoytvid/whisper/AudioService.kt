package src.charleex.autoytvid.whisper

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.core.writeFully
import okio.buffer
import okio.use
import src.charleex.autoytvid.api.openapi.OpenAiApi
import src.charleex.autoytvid.whisper.model.FileSource
import src.charleex.autoytvid.whisper.model.ResponseFormat
import src.charleex.autoytvid.whisper.model.transcription.Transcription
import src.charleex.autoytvid.whisper.model.transcription.TranscriptionRequest
import src.charleex.autoytvid.whisper.model.translation.Translation
import src.charleex.autoytvid.whisper.model.translation.TranslationRequest

private const val API_PATH_TRANSLATION = "audio/translations"
private const val API_PATH_TRANSCRIPTION = "audio/transcriptions"

internal interface AudioService {
    suspend fun transcription(request: TranscriptionRequest): Transcription
    suspend fun translation(request: TranslationRequest): Translation
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

    override suspend fun translation(request: TranslationRequest): Translation {
        return when (request.responseFormat) {
            ResponseFormat.JSON.name.lowercase(),
            ResponseFormat.VERBOSE_JSON.name.lowercase(),
            null,
            -> translationAsJson(request)

            else -> translationAsString(request)
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

    private suspend fun translationAsJson(request: TranslationRequest): Translation {
        return try {
            requester.perform { client ->
                client.submitFormWithBinaryData(
                    url = API_PATH_TRANSLATION,
                    formData = formDataOf(request)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Translation(e.localizedMessage ?: "translationAsJson error")
        }
    }

    private suspend fun translationAsString(request: TranslationRequest): Translation {
        return try {
            val text = requester.perform<String> { client ->
                client.submitFormWithBinaryData(
                    url = API_PATH_TRANSLATION,
                    formData = formDataOf(request)
                )
            }
            Translation(text)
        } catch (e: Exception) {
            e.printStackTrace()
            Translation(e.localizedMessage ?: "translationAsString error")
        }
    }

    private fun formDataOf(request: TranslationRequest) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        request.responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
    }
}

internal suspend inline fun <reified T> OpenAiApi.perform(noinline block: suspend (HttpClient) -> HttpResponse): T {
    return perform(typeInfo<T>(), block)
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

