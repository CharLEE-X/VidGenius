package com.charleex.vidgenius.datasource.feature.vision_ai

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
import org.koin.dsl.module

fun visionAiModule() = module {
    single<VisionAiService> {
        VisionAiServiceImpl(
            logger = withTag(VisionAiService::class.simpleName!!),
        )
    }
    single<GoogleCloudRepository> {
        if (getIsDebugBuild()) GoogleCloudRepositoryDebug().also { println("GoogleCloudRepository in DEBUG mode") }
        else GoogleCloudRepositoryImpl(
            logger = withTag(GoogleCloudRepository::class.simpleName!!),
            database = get(),
            visionAiService = get(),
        ).also { println("GoogleCloudRepository in RELEASE mode") }
    }
}

