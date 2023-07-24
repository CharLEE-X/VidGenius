package com.charleex.autoytvid.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.autoytvid.feature.videoscreenshots.VideoScreenshotsContract
import com.charleex.autoytvid.feature.videoscreenshots.VideoScreenshotsViewModel
import com.charleex.autoytvid.ui.components.AppCard
import com.charleex.autoytvid.ui.components.ImageFromBufferedImage
import com.charleex.autoytvid.ui.util.Breakpoint

@Composable
internal fun VideoScreenshotsContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    filePath: String,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        VideoScreenshotsViewModel(
            scope = scope,
            filePath = filePath,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()

    AppCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = state.name,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = state.path,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = "${state.duration}s",
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            AnimatedVisibility(visible = !state.processing && state.screenshots.isEmpty()) {
                Button(
                    onClick = { vm.trySend(VideoScreenshotsContract.Inputs.GetScreenshots) },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.Screenshot,
                        contentDescription = "Get screenshots",
                    )
                    Text(
                        text = "Get screenshots",
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
            AnimatedVisibility(visible = state.processing) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                state.screenshots.forEach { screenshot ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .width(200.dp)
                            .height(400.dp)
                    ) {
                        ImageFromBufferedImage(
                            file = screenshot,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
