package com.charleex.vidgenius.vision_ai

import co.touchlab.kermit.Logger.Companion.withTag
import org.koin.dsl.module

fun visionAiModule() = module {
    single<VisionAiService> {
        VisionAiServiceImpl(
            logger = withTag(VisionAiService::class.simpleName!!),
        )
    }
//    single<JacksonFactory> {
//        JacksonFactory()
//    }
}

