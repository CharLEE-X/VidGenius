package com.charleex.vidgenius.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.root.RootViewModel
import com.charleex.vidgenius.feature.router.RouterScreen
import com.charleex.vidgenius.ui.AppState
import com.charleex.vidgenius.ui.components.KXSnackBarHost
import com.charleex.vidgenius.ui.theme.AutoYtVidTheme
import com.charleex.vidgenius.ui.util.Breakpoint
import kotlinx.coroutines.launch

@Composable
fun RootContent(
    modifier: Modifier,
    window: ComposeWindow
) {
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val vm = remember(scope) {
        RootViewModel(
            scope = scope,
        )
    }
    val state by vm.observeStates().collectAsState()

    // TODO: Handle breakpoint in VM
    val currentBreakpoint by AppState.currentBreakpoint.collectAsState(Breakpoint.DESKTOP_SMALL)

    LaunchedEffect(currentBreakpoint) {
        println("Current breakpoint: $currentBreakpoint")
    }

    val initialRoute = when (state.isAuthenticated) {
        true -> RouterScreen.ProcessVideo
        false -> RouterScreen.Login
    }

    AutoYtVidTheme {
        BoxWithConstraints(
            modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .onSizeChanged {
                    AppState.windowSize.value = AppState.windowSize.value.copy(
                        width = it.width.dp,
                        height = it.height.dp
                    )
                }
        ) {
            RouterContent(
                modifier = modifier,
                isAuthenticated = state.isAuthenticated,
                breakpoint = currentBreakpoint,
                initialRoute = initialRoute,
                displayMessage = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                },
                window = window,
            )
            KXSnackBarHost(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
