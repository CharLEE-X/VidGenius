package com.charleex.autoytvid.ui.features

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import com.charleex.autoytvid.feature.router.RouterScreen
import com.charleex.autoytvid.feature.router.RouterViewModel
import com.charleex.autoytvid.ui.components.AppScaffold
import com.charleex.autoytvid.ui.components.KXSnackBarHost
import com.charleex.autoytvid.ui.components.AppTopBar
import com.charleex.autoytvid.ui.util.Breakpoint
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.currentRouteOrNull
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.pathParameter
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import com.copperleaf.ballast.navigation.routing.stringPath
import com.copperleaf.ballast.navigation.vm.Router

@Composable
internal fun RouterContent(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    isAuthenticated: Boolean,
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val initialRoute = when (isAuthenticated) {
        true -> RouterScreen.FeatureList
        false -> RouterScreen.Login
    }
    val router: Router<RouterScreen> =
        remember(scope) {
            RouterViewModel(
                viewModelScope = scope,
                initialRoute = initialRoute,
            )
        }
    val routerState: Backstack<RouterScreen> by router.observeStates().collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            if (routerState.currentRouteOrNull != RouterScreen.Login) {
                println("User is not authenticated, redirecting to login screen")
                router.trySend(RouterContract.Inputs.ReplaceTopDestination(RouterScreen.Login.matcher.routeFormat))
            }
        } else {
            if (routerState.currentRouteOrNull == RouterScreen.Login) {
                println("User is authenticated, redirecting to dashboard screen")
                router.trySend(RouterContract.Inputs.ReplaceTopDestination(RouterScreen.FeatureList.matcher.routeFormat))
            }
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
//                currentRouteTitle = routerState.currentRouteOrNull?.label() ?: "",
                routerScreen = routerState.currentRouteOrNull,
                onBackClicked = { router.trySend(RouterContract.Inputs.GoBack()) },
                backEnabled = routerState.size > 1,
                extrasEnd = {},
            )
        },
        snackbarHost = { KXSnackBarHost(snackbarHostState = snackbarHostState) },
        modifier = Modifier
    ) {
        routerState.renderCurrentDestination(
            route = { routerScreen: RouterScreen ->
                when (routerScreen) {
                    RouterScreen.FeatureList -> FeatureList(
                        onGotToLogin = {
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.Login
                                        .directions()
                                        .build()
                                )
                            )
                        },
                        onGoToDragDrop = {
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.DragDrop
                                        .directions()
                                        .build()
                                )
                            )
                        },
                        onGoToVideoList = {
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.VideoList
                                        .directions()
                                        .build()
                                )
                            )
                        },
                        onGotToVideoDetail = { id ->
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.VideoDetail
                                        .directions()
                                        .pathParameter("id", id)
                                        .build()
                                )
                            )
                        },
                    )
                    RouterScreen.Login -> LoginContent(
                        modifier = modifier,
                        breakpoint = breakpoint,
                        displayMessage = displayMessage,
                    )

                    RouterScreen.VideoList -> VideoListContent(
                        breakpoint = breakpoint,
                        displayMessage = displayMessage,
                        goToDragAndDrop = {
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.DragDrop
                                        .directions()
                                        .build()
                                )
                            )
                        },
                        goToVideoDetail = { id ->
                            router.trySend(
                                RouterContract.Inputs.GoToDestination(
                                    RouterScreen.VideoDetail
                                        .directions()
                                        .pathParameter("id", id)
                                        .build()
                                )
                            )
                        },
                    )

                    RouterScreen.VideoDetail -> {
                        val id: String by stringPath()
                        VideoDetailContent(
                            videoId = id,
                            breakpoint = breakpoint,
                            displayMessage = displayMessage,
                        )
                    }

                    RouterScreen.DragDrop -> DragDropContent(
                        breakpoint = breakpoint,
                        displayMessage = displayMessage,
                        window = window,
                    )
                }
            },
            notFound = { },
        )
    }
}
