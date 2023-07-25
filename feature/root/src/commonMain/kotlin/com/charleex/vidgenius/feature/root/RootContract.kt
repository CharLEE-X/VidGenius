package com.charleex.vidgenius.feature.root

object RootContract {
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

