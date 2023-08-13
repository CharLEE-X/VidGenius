package com.charleex.vidgenius.open_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.open_ai.api.OpenAiApi
import com.charleex.vidgenius.open_ai.api.OpenAiApiImpl
import com.charleex.vidgenius.open_ai.api.OpenAiConfig
import com.charleex.vidgenius.open_ai.client.createHttpClient
import com.charleex.vidgenius.open_ai.model.ModelId
import org.koin.dsl.module

fun openAiModule(isDebugBuild: Boolean) = module {
    val openAiConfig = OpenAiConfig()

    single<OpenAiApi> {
        OpenAiApiImpl(
            httpClient = createHttpClient(openAiConfig)
        )
    }
    single<ChatService> {
        ChatServiceImpl(
            requester = get(),
            modelId = ModelId("gpt-3.5-turbo"),
        )
    }
    single<OpenAiRepository> {
        if (isDebugBuild) OpenAiRepositoryDebug().also { println("OpenAiRepository in DEBUG mode") }
        else OpenAiRepositoryImpl(
            logger = Logger.withTag(OpenAiRepository::class.simpleName!!),
            chatService = get(),
        ).also { println("OpenAiRepository in RELEASE mode") }
    }
}
