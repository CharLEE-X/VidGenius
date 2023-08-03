import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.datasourceModule
import com.charleex.vidgenius.ui.features.process.ProcessVideosContent
import org.koin.core.context.startKoin

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalComposeApi
fun main() = application {
    val koinApplication = startKoin {
        modules(datasourceModule)
    }
    val videoProcessing by koinApplication.koin.inject<VideoProcessing>()

    val windowState = rememberWindowState()
    windowState.apply {
        size = DpSize(1200.dp, 1400.dp)
        position = WindowPosition(
            alignment = Alignment.Center,
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Auto Yt Vid"
    ) {
        ProcessVideosContent(
            videoProcessing = videoProcessing,
            window = window,
        )
    }
}
