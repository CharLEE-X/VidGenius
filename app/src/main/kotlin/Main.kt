import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charleex.vidgenius.datasource.datasourceModule
import com.charleex.vidgenius.datasource.model.ChannelConfig
import com.charleex.vidgenius.ui.features.root.RootContent
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

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
            width = 1600.dp,
            height = 1400.dp
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
            animalVideoProcessing = koinApplication.koin.get(named(ChannelConfig.Fails().id)),
            failsVideoProcessing = koinApplication.koin.get(named(ChannelConfig.Fails().id)),
            configManager = koinApplication.koin.get(),
            window = window,
        )
    }
}
