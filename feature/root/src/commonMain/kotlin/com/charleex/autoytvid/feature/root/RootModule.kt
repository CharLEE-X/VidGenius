package com.charleex.autoytvid.feature.root

import org.koin.dsl.module
import src.charleex.autoytvid.repository.repositoryModule

val rootModule = module {
    includes(repositoryModule)

    single {
        RootViewModel(
            scope = get()
        )
    }
}
