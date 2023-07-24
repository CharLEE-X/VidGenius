package src.charleex.autoytvid.whisper.model

import okio.FileSystem
import okio.Path
import okio.Source

class FileSource(
    val name: String,
    val source: Source,
) {
    constructor(path: Path, fileSystem: FileSystem) : this(path.name, fileSystem.source(path))
}
