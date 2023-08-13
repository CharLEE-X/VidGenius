package src.charleex.vidgenius.datasource

import java.io.File

object TestData {
    val image1 get() = getResource("screenshots/image1.png")
    val image2 get() = getResource("screenshots/image2.png")
    val image3 get() = getResource("screenshots/image3.png")

    private fun getResource(path: String) =
        this.javaClass.classLoader.getResource(path)?.file?.let(::File)!!
}
