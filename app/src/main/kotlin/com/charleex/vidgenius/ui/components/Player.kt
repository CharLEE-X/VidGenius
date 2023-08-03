package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.VideoPlayer
import com.charleex.vidgenius.ui.util.rememberVideoPlayerState
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

/**
 * To play a local file, use a URL notation like this:
 * ```kotlin
 * const val VIDEO_URL = "file:///C:/Users/John/Desktop/example.mp4"
 * ```
 * Relative paths like this may also work (relative to subproject directory aka `demo/`):
 * ```kotlin
 * val VIDEO_URL = """file:///${Path("videos/example.mp4")}"""
 * ```
 * To package a video with the app distributable,
 * see [this tutorial](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#adding-files-to-packaged-application)
 * and then use a URL syntax like this:
 * ```kotlin
 * val VIDEO_URL = """file:///${Path(System.getProperty("compose.application.resources.dir")) / "example.mp4"}"""
 * ```
 */
const val VIDEO_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

@Composable
internal fun Player(
    modifier: Modifier = Modifier,
    url: String,
    onTimestampChange: (timestamp: Long) -> Unit
) {
    val state = rememberVideoPlayerState()

    var videoPath by remember { mutableStateOf(url) }

    LaunchedEffect(url) {
        videoPath = url
    }

    LaunchedEffect(state.progress.value.timeMillis) {
        onTimestampChange(state.progress.value.timeMillis)
    }

    /*
     * Could not use a [Box] to overlay the controls on top of the video.
     * See https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Swing_Integration
     * Related issues:
     * https://github.com/JetBrains/compose-multiplatform/issues/1521
     * https://github.com/JetBrains/compose-multiplatform/issues/2926
     */
    Column {
        Surface(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                if (url.isNotEmpty()) {
                    VideoPlayer(
                        url = url,
                        state = state,
                        onFinish = state::stopPlayback,
                        modifier = modifier.fillMaxSize()
                    )
                }
                if (url.isEmpty()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        Slider(
            value = state.progress.value.fraction,
            onValueChange = { state.seek = it },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val time = state.progress.value.timeMillis.milliseconds.toDouble(DurationUnit.SECONDS)
            Text("Time: $time s", modifier = Modifier.width(180.dp))
            IconButton(onClick = state::toggleResume) {
                Icon(
                    painter = painterResource("${if (state.isResumed) "pause" else "play"}.svg"),
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(32.dp)
                )
            }
//            IconButton(onClick = state::toggleFullscreen) {
//                Icon(
//                    painter = painterResource("${if (state.isFullscreen) "exit" else "enter"}-fullscreen.svg"),
//                    contentDescription = "Toggle fullscreen",
//                    modifier = Modifier.size(32.dp)
//                )
//            }
//            Speed(
//                initialValue = state.speed,
//                modifier = Modifier.width(104.dp)
//            ) {
//                state.speed = it ?: state.speed
//            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource("volume.svg"),
                    contentDescription = "Volume",
                    modifier = Modifier.size(32.dp)
                )
                // TODO: Make the slider change volume in logarithmic manner
                //  See https://www.dr-lex.be/info-stuff/volumecontrols.html
                //  and https://ux.stackexchange.com/q/79672/117386
                //  and https://dcordero.me/posts/logarithmic_volume_control.html
                Slider(
                    value = state.volume,
                    onValueChange = { state.volume = it },
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

/**
 * See [this Stack Overflow post](https://stackoverflow.com/a/67765652).
 */
@Composable
fun Speed(
    initialValue: Float,
    modifier: Modifier = Modifier,
    onChange: (Float?) -> Unit
) {
    var input by remember { mutableStateOf(initialValue.toString()) }
    OutlinedTextField(
        value = input,
        modifier = modifier,
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource("speed.svg"),
                contentDescription = "Speed",
                modifier = Modifier.size(28.dp)
            )
        },
        onValueChange = {
            input = if (it.isEmpty()) {
                it
            } else if (it.toFloatOrNull() == null) {
                input // Old value
            } else {
                it // New value
            }
            onChange(input.toFloatOrNull())
        }
    )
}
