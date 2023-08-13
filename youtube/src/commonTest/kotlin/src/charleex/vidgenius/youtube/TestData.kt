package src.charleex.vidgenius.youtube

import java.io.File

internal object TestData {

    val authDir = getResource("auth")

    private fun getResource(path: String) =
        this.javaClass.classLoader.getResource(path)?.file?.let(::File)!!
}
