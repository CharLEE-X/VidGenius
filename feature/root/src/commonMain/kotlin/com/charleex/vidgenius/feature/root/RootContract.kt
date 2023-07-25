package com.charleex.vidgenius.feature.root

import org.koin.core.component.KoinComponent

object RootContract : KoinComponent {
    data class State(
        val isAuthenticated: Boolean = true,
        val isLoading: Boolean = true,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class IsAuthenticated(val isAuthenticated: Boolean) : Update
            data class IsLoading(val isLoading: Boolean) : Update
        }

        object Init : Inputs
        object MonitorAuthState : Inputs
    }

    sealed interface Events {
        object NotAuthenticated : Events
        data class Authenticated(val token: String) : Events
    }
}

