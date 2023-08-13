package src.charleex.vidgenius.open_ai

import java.io.File

object TestData {


    private fun getResource(path: String) =
        this.javaClass.classLoader.getResource(path)?.file?.let(::File)!!
}
