package src.charleex.autoytvid.whisper

import org.koin.dsl.module
import src.charleex.autoytvid.api.apiModule
import src.charleex.autoytvid.whisper.model.ModelId

val whisperModule = module {
    includes(apiModule)

    single<AudioService> {
        AudioServiceImpl(
            requester = get(),
        )
    }
    single<TranslationService> {
        TranslationServiceImpl(
            audioService = get(),
            model = ModelId("whisper-1"),
        )
    }
    single<TranscriptionService> {
        TranscriptionServiceImpl(
            audioService = get(),
            model = ModelId("whisper-1"),
        )
    }
    single<ChatService> {
        ChatServiceImpl(
            requester = get(),
            modelId = ModelId("gpt-3.5-turbo-0613"),
        )
    }
}
