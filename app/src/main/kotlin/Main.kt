import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charleex.vidgenius.ui.features.RootContent
import com.charleex.vidgenius.ui.initKoin

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalComposeApi
fun main() = application {
    initKoin()

    val windowState = rememberWindowState()
    windowState.apply {
        size = DpSize(1200.dp, 1400.dp)
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
            window = window,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
