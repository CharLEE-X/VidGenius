package src.charleex.autoytvid.api

import org.koin.dsl.module
import src.charleex.autoytvid.api.client.createHttpClient
import src.charleex.autoytvid.api.monto_api.MontoApi
import src.charleex.autoytvid.api.monto_api.MontoApiImpl
import src.charleex.autoytvid.api.monto_api.MontoConfig
import src.charleex.autoytvid.api.openapi.OpenAiApi
import src.charleex.autoytvid.api.openapi.OpenAiApiImpl
import src.charleex.autoytvid.api.openapi.OpenAiConfig

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
