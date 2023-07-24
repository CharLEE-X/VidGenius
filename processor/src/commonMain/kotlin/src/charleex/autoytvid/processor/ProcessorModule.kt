package src.charleex.autoytvid.processor

import co.touchlab.kermit.Logger.Companion.withTag
import org.koin.dsl.module

val processorModule = module {
    single<FileProcessor> {
        FileProcessorImpl(
            logger = withTag(FileProcessor::class.simpleName!!),
        )
    }
}
