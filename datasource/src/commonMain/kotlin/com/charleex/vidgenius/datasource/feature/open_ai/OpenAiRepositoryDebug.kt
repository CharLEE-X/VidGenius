package com.charleex.vidgenius.datasource.feature.open_ai

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.open_ai.model.ModelId
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionChunk
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatCompletionFunction
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.ChatMessage
import com.charleex.vidgenius.datasource.feature.open_ai.model.chat.FunctionMode
import com.charleex.vidgenius.datasource.model.ChannelConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class OpenAiRepositoryDebug : OpenAiRepository {
    override suspend fun getDescriptionContext(video: Video): Video {
        delay(100)
        return video.copy(descriptionContext = "description context")
    }

    override suspend fun getMetaData(video: Video): Video {
        delay(300)
        return video.copy(
            title = "title",
            description = "description",
            tags = listOf("tag1", "tag2"),
        )
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
}
