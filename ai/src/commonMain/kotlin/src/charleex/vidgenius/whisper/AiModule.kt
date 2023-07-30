package src.charleex.vidgenius.whisper

import org.koin.dsl.module
import src.charleex.vidgenius.api.apiModule
import src.charleex.vidgenius.whisper.model.ModelId

val openAiModule = module {
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
            modelId = ModelId("gpt-3.5-turbo"),
        )
    }
}
