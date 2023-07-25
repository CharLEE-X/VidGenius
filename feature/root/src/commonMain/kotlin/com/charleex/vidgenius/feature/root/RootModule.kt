package com.charleex.vidgenius.feature.root

import org.koin.dsl.module
import src.charleex.vidgenius.repository.repositoryModule

val rootModule = module {
    includes(repositoryModule)

    single {
        RootViewModel(
            scope = get()
        )
    }
}
