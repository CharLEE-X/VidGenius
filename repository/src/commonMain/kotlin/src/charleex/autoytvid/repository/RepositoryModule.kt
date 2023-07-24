package src.charleex.autoytvid.repository

import co.touchlab.kermit.Logger.Companion.withTag
import com.charleex.autoytvid.yt.youtubeModule
import org.koin.dsl.module
import src.charleex.autoytvid.api.apiModule
import src.charleex.autoytvid.processor.processorModule
import src.charleex.autoytvid.whisper.whisperModule

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
