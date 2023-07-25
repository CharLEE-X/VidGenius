package com.charleex.vidgenius.feature.root

import org.koin.dsl.module
import com.charleex.vidgenius.datasource.di.repositoryModule

val rootModule = module {
    includes(repositoryModule)

    single {
        RootViewModel(
            scope = get()
        )
    }
}
