package com.charleex.vidgenius.datasource.feature.open_ai

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.open_ai.model.ContentInfo
import com.charleex.vidgenius.datasource.feature.open_ai.model.ModelId
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionChunk
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionFunction
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.FunctionMode
import com.charleex.vidgenius.datasource.feature.youtube.model.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class OpenAiRepositoryDebug : OpenAiRepository {
    override suspend fun getDescriptionContext(video: Video, category: Category): Video {
        delay(100)
        return video.copy(descriptionContext = "description context")
    }

    override suspend fun getMetaData(video: Video, category: Category): Video {
        delay(300)
        return video
    }

    override fun chats(
        messages: List<ChatMessage>,
        temperature: Double?,
        topP: Double?,
        n: Int?,
        stop: List<String>?,
        maxTokens: Int?,
        presencePenalty: Double?,
        frequencyPenalty: Double?,
        logitBias: Map<String, Int>?,
        user: String?,
        functions: List<ChatCompletionFunction>?,
        functionCall: FunctionMode?,
    ): Flow<ChatCompletionChunk> {
        return flowOf(
            ChatCompletionChunk(
                "debug message",
                1,
                model = ModelId(""),
                choices = listOf()
            )
        )
    }

    override suspend fun getContentInfo(
        title: String,
        description: String?,
        tags: List<String>,
    ): ContentInfo {
        TODO("Not yet implemented")
    }
}
