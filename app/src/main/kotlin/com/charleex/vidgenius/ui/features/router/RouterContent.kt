package com.charleex.vidgenius.ui.features.router

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.features.generation.GenerationContent
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import com.copperleaf.ballast.navigation.vm.Router

@Composable
internal fun RouterContent(
    displayMessage: (String) -> Unit,
    videoService: VideoService,
    configManager: ConfigManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val router: Router<RouterScreen> =
        remember(scope) {
            RouterViewModel(
                viewModelScope = scope,
                initialRoute = RouterScreen.Generation,
            )
        }
    val routerState: Backstack<RouterScreen> by router.observeStates().collectAsState()

    Surface(
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        routerState.renderCurrentDestination(
            route = { routerScreen: RouterScreen ->
                when (routerScreen) {
                    RouterScreen.Dashboard -> {

                    }

                    RouterScreen.Generation -> {
                        GenerationContent(
                            videoService = videoService,
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
