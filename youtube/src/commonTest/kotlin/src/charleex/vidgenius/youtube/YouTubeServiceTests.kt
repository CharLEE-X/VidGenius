package src.charleex.vidgenius.youtube

import app.cash.turbine.test
import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.datasource.feature.youtube.CREDENTIALS_DIRECTORY
import com.charleex.vidgenius.datasource.feature.youtube.YouTubeService
import com.charleex.vidgenius.datasource.feature.youtube.YouTubeServiceImpl
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuthImpl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test

class YouTubeServiceTests {
    private lateinit var sut: YouTubeService

//    @Mock
//    private val googleAuth = mock(classOf<GoogleAuth>())


    private fun googleAuth(): GoogleAuth {
        val userHomeDir = System.getProperty("user.home")
        val appDataDir = File(userHomeDir, "VidGeniusAppData")
        if (!appDataDir.exists()) {
            appDataDir.mkdir()
        }
        return GoogleAuthImpl(
            logger = withTag("GoogleAuth"),
            appDataDir = appDataDir.absolutePath,
            httpTransport = NetHttpTransport(),
            jsonFactory = JacksonFactory(),
            credentialDirectory = CREDENTIALS_DIRECTORY,
        )
    }

    val config = "/youtube-animals.json"

    private fun startTest(
        block: suspend () -> Unit,
    ) = runTest {
//        given(googleAuth)
//            .function(googleAuth::authorizeYouTube)
//            .whenInvokedWith(any())
//            .then { }

        sut = YouTubeServiceImpl(
            logger = withTag("YouTubeService"),
            googleAuth = googleAuth(),
            httpTransport = NetHttpTransport(),
            jsonFactory = JacksonFactory(),
        )

        block()
    }

    @Test
    fun `test `() = startTest {
        sut.getUploadList(config).test {
            val item1 = awaitItem()
            println("item1: $item1")
            val item2 = awaitItem()
            println("item2: $item2")
        }
    }
}
