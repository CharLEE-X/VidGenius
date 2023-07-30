package com.charleex.vidgenius.yt

import com.charleex.vidgenius.youtube.youtubeModule
import com.charleex.vidgenius.yt.mock.channelUploadServiceMock
import com.charleex.vidgenius.youtube.youtube.video.ChannelUploadsService
import kotlinx.coroutines.test.runTest
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ChannelUploadsServiceTest : KoinTest {
    private lateinit var sut: ChannelUploadsService

    @BeforeTest
    fun setup() {
        stopKoin()
        startKoin {
            modules(youtubeModule())
            loadKoinModules(
                module {
                    single<ChannelUploadsService> {
                        channelUploadServiceMock
                    }
                }
            )
        }
        sut = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test upload list`() = runTest {
        val list = sut.getUploadList()
        println("List: $list")
        assertEquals(true, list.isNotEmpty())
    }
}
