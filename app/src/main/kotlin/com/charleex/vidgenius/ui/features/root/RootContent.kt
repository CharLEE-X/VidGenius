package com.charleex.vidgenius.ui.features.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.adrianwitaszak.shoppe.composeui.feature.router.RouterContent
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.ConfigManager
import com.charleex.vidgenius.ui.AppState
import com.charleex.vidgenius.ui.theme.AutoYtVidTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(
    animalVideoProcessing: VideoProcessing,
    failsVideoProcessing: VideoProcessing,
    configManager: ConfigManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    AutoYtVidTheme {
        BoxWithConstraints(
            modifier =
            Modifier
                .fillMaxSize()
                .onSizeChanged {
                    AppState.windowSize.value =
                        AppState.windowSize.value.copy(
                            width = it.width.dp,
                            height = it.height.dp
                        )
                }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    RouterContent(
                        animalsVideoProcessing = animalVideoProcessing,
                        failsVideoProcessing = animalVideoProcessing,
                        configManager = configManager,
                        window = window,
                        displayMessage = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                    )
                }
            }
        }
    }
}
