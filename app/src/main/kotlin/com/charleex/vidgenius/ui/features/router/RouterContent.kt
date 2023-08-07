package com.charleex.vidgenius.ui.features.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.awt.ComposeWindow
import com.charleex.vidgenius.datasource.ConfigManager
import com.charleex.vidgenius.datasource.UploadsManager
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepository
import com.charleex.vidgenius.ui.components.NavigationSheet
import com.charleex.vidgenius.ui.features.generation.AnimalsGenerationContent
import com.charleex.vidgenius.ui.features.generation.FailsGenerationContent
import com.charleex.vidgenius.ui.features.settings.SettingsContent
import com.charleex.vidgenius.ui.features.subtitles.AnimalsSubtitlesContent
import com.charleex.vidgenius.ui.features.subtitles.FailsSubtitlesContent
import com.charleex.vidgenius.ui.features.uploads.AnimalsUploadsContent
import com.charleex.vidgenius.ui.features.uploads.FailsUploadsContent
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
    animalsFileRepository: VideoFileRepository,
    failsFileRepository: VideoFileRepository,
    animalsVideoProcessing: VideoProcessing,
    failsVideoProcessing: VideoProcessing,
    animalsUploadsManager: UploadsManager,
    failsUploadsManager: UploadsManager,
    configManager: ConfigManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val router: Router<RouterScreen> =
        remember(scope) {
            RouterViewModel(
                viewModelScope = scope,
                initialRoute = RouterScreen.AnimalsGeneration,
            )
        }
    val routerState: Backstack<RouterScreen> by router.observeStates().collectAsState()

    NavigationSheet(
        routerScreen = routerState.currentRouteOrNull ?: RouterScreen.Dashboard,
        animalsUploadsManager = animalsUploadsManager,
        failsUploadsManager = failsUploadsManager,
        animalsVideoProcessing = animalsVideoProcessing,
        failsVideoProcessing = failsVideoProcessing,
        onGoToAnimalUploads = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.AnimalsUploads
                        .directions()
                        .build()
                )
            )
        },
        onGoToAnimalGeneration = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.AnimalsGeneration
                        .directions()
                        .build()
                )
            )
        },
        onGoToAnimalSubtitles = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.AnimalsSubtitles
                        .directions()
                        .build()
                )
            )
        },
        onGoToFailsUploads = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.FailsUploads
                        .directions()
                        .build()
                )
            )
        },
        onGoToFailsGeneration = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.FailsGeneration
                        .directions()
                        .build()
                )
            )
        },
        onGoToFailsSubtitles = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.FailsSubtitles
                        .directions()
                        .build()
                )
            )
        },
        onGoToSettings = {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    RouterScreen.Settings
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

                    RouterScreen.Settings -> {
                        SettingsContent()
                    }

                    RouterScreen.AnimalsUploads -> {
                        AnimalsUploadsContent(
                            uploadsManager = animalsUploadsManager,
                        )
                    }

                    RouterScreen.AnimalsGeneration -> {
                        AnimalsGenerationContent(
                            videoFileRepository = animalsFileRepository,
                            videoProcessing = animalsVideoProcessing,
                            uploadsManager = animalsUploadsManager,
                            configManager = configManager,
                            window = window,
                            displayMessage = displayMessage,
                        )
                    }

                    RouterScreen.AnimalsSubtitles -> {
                        AnimalsSubtitlesContent(
                        )
                    }

                    RouterScreen.FailsUploads -> {
                        FailsUploadsContent(
                            uploadsManager = failsUploadsManager,
                        )
                    }

                    RouterScreen.FailsGeneration -> {
                        FailsGenerationContent(
                            videoFileRepository = failsFileRepository,
                            videoProcessing = failsVideoProcessing,
                            uploadsManager = failsUploadsManager,
                            configManager = configManager,
                            window = window,
                            displayMessage = displayMessage,
                        )
                    }

                    RouterScreen.FailsSubtitles -> {
                        FailsSubtitlesContent(
                        )
                    }
                }
            },
            notFound = { },
        )
    }
}
