package com.charleex.vidgenius.datasource.feature.vision_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.utils.getIsDebugBuild
import org.koin.dsl.module


internal val visionAiModule
    get() = module {
        single<VisionAiService> {
            VisionAiServiceImpl(
                logger = Logger.withTag(VisionAiService::class.simpleName!!),
            )
        }
        single<GoogleCloudRepository> {
            if (getIsDebugBuild()) GoogleCloudRepositoryDebug().also { println("GoogleCloudRepository in DEBUG mode") }
            else GoogleCloudRepositoryImpl(
                logger = Logger.withTag(GoogleCloudRepository::class.simpleName!!),
                database = get(),
                visionAiService = get(),
            ).also { println("GoogleCloudRepository in RELEASE mode") }
        }
    }
