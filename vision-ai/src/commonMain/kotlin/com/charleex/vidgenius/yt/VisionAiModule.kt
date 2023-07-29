package com.charleex.vidgenius.yt

import co.touchlab.kermit.Logger.Companion.withTag
import com.google.api.client.json.jackson2.JacksonFactory
import org.koin.dsl.module

private val GOOGLE_CREDENTIALS_JSON =
    "/Users/adrianwitaszak/.config/gcloud/application_default_credentials.json"

fun visionAiModule() = module {
    single<VisionAiService> {
        VisionAiServiceImpl(
            logger = withTag(VisionAiService::class.simpleName!!),
        )
    }
    single<JacksonFactory> {
        JacksonFactory()
    }
}

