package com.charleex.vidgenius.vision_ai

import co.touchlab.kermit.Logger
import org.koin.dsl.module

fun visionAiModule(isDebugBuild: Boolean) = module {
    single<VisionAiApi> {
        VisionAiApiImpl(
            logger = Logger.withTag(VisionAiApi::class.simpleName!!),
        )
    }
    single<GoogleCloudRepository> {
        if (isDebugBuild) GoogleCloudRepositoryDebug().also { println("GoogleCloudRepository in DEBUG mode") }
        else GoogleCloudRepositoryImpl(
            logger = Logger.withTag(GoogleCloudRepository::class.simpleName!!),
            visionAiApi = get(),
        ).also { println("GoogleCloudRepository in RELEASE mode") }
    }
}
