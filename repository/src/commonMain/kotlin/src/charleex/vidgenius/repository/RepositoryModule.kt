package src.charleex.vidgenius.repository

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.vidgenius.yt.youtubeModule
import org.koin.dsl.module
import src.charleex.vidgenius.api.apiModule
import src.charleex.vidgenius.processor.processorModule
import src.charleex.vidgenius.whisper.whisperModule

val repositoryModule = module {
    includes(
        whisperModule,
        apiModule,
        processorModule,
        youtubeModule(),
    )
    single<AssistRepository> {
        AssistRepositoryImpl(
            montoApi = get(),
            transcriptionService = get(),
            translationService = get(),
            chatService = get(),
        )
    }
    single<YoutubeRepository> {
        YoutubeRepositoryImpl(
            logger = withTag(YoutubeRepository::class.simpleName!!),
            channelUploadsService = get(),
        )
    }
}
