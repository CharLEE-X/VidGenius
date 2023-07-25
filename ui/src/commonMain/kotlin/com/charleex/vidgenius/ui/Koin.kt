package com.charleex.vidgenius.ui

import com.charleex.vidgenius.feature.root.rootModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(additionalModules: List<Module> = emptyList()): KoinApplication {
    val koinApplication = startKoin {
        modules(
            additionalModules + composeUiModule
        )
    }
    return koinApplication
}

internal val composeUiModule = module {
    includes(rootModule)
}
