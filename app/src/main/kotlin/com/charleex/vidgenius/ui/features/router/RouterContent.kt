package com.charleex.vidgenius.ui.features.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.awt.ComposeWindow
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.NavigationSheet
import com.charleex.vidgenius.ui.features.generation.AnimalsGenerationContent
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.currentRouteOrNull
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import com.copperleaf.ballast.navigation.vm.Router

@Composable
internal fun RouterContent(
    displayMessage: (String) -> Unit,
    videoProcessing: VideoProcessing,
    configManager: ConfigManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val router: Router<RouterScreen> =
        remember(scope) {
            RouterViewModel(
                viewModelScope = scope,
                initialRoute = RouterScreen.Dashboard,
            )
        }
    val routerState: Backstack<RouterScreen> by router.observeStates().collectAsState()

    NavigationSheet(
        routerScreen = routerState.currentRouteOrNull ?: RouterScreen.Dashboard,
        onGoToGeneration = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.Generation
                        .directions()
                        .build()
                )
            )
        },
        onGoToDashboard = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.Dashboard
                        .directions()
                        .build()
                )
            )
        },
    ) {
        routerState.renderCurrentDestination(
            route = { routerScreen: RouterScreen ->
                when (routerScreen) {
                    RouterScreen.Dashboard -> {

                    }

                    RouterScreen.Generation -> {
                        AnimalsGenerationContent(
                            videoProcessing = videoProcessing,
                            configManager = configManager,
                            window = window,
                            displayMessage = displayMessage,
                        )
                    }
                }
            },
            notFound = { },
        )
    }
}
