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
import com.lt.load_the_image.LoadTheImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalComposeApi
fun main() = application {
    LoadTheImageManager.defaultErrorImagePath = "load_error.jpeg"

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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
        onCloseRequest = {
            scope.cancel()
            exitApplication()
        },
        state = windowState,
        title = "Auto Yt Vid"
    ) {
        RootContent(
            videoService = koinApplication.koin.get { parametersOf(scope) },
            configManager = koinApplication.koin.get(),
            window = window,
        )
    }
}
