import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charleex.vidgenius.datasource.datasourceModule
import com.charleex.vidgenius.ui.features.root.RootContent
import org.koin.core.context.startKoin

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalComposeApi
fun main() = application {
    val koinApplication = startKoin {
        modules(datasourceModule())
    }

    val windowState = rememberWindowState()
    windowState.apply {
        size = DpSize(
            width = 1200.dp,
            height = 1600.dp
        )
//        position = WindowPosition(
//            alignment = Alignment.Center,
//        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Auto Yt Vid"
    ) {
        RootContent(
            videoProcessing = koinApplication.koin.get(),
            youtubeRepository = koinApplication.koin.get(),
            configManager = koinApplication.koin.get(),
            window = window,
        )
    }
}
