package src.charleex.vidgenius.api

import org.koin.dsl.module
import src.charleex.vidgenius.api.client.createHttpClient
import src.charleex.vidgenius.api.monto_api.MontoApi
import src.charleex.vidgenius.api.monto_api.MontoApiImpl
import src.charleex.vidgenius.api.monto_api.MontoConfig
import src.charleex.vidgenius.api.openapi.OpenAiApi
import src.charleex.vidgenius.api.openapi.OpenAiApiImpl
import src.charleex.vidgenius.api.openapi.OpenAiConfig

val apiModule = module {
    val openAiConfig = OpenAiConfig()
    val montoConfig = MontoConfig()

    single<OpenAiApi> {
        OpenAiApiImpl(
            httpClient = createHttpClient(openAiConfig)
        )
    }
    single<MontoApi> {
        MontoApiImpl(
            httpClient = createHttpClient(montoConfig)
        )
    }
}
