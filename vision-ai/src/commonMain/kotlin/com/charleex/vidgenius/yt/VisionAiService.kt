package com.charleex.vidgenius.yt

import co.touchlab.kermit.Logger
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import java.io.File
import java.io.IOException


interface VisionAiService {
    fun getTextFromImage(imagePath: String): String
}

class VisionAiServiceImpl(
    private val logger: Logger,
) : VisionAiService {
    override fun getTextFromImage(imagePath: String): String {
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            throw NoSuchFileException(
                file = imageFile,
                reason = "The file you specified does not exist"
            )
        }

        return try {
            quickstart(imageFile)
        } catch (e: IOException) {
            logger.e { e.message ?: "Image annotation failed." }
            ""
        }
    }

    private fun quickstart(imageFile: File): String {
        val imgProto = ByteString.copyFrom(imageFile.readBytes())

        val imageAnnotatorClient = ImageAnnotatorClient.create()

        // Set up the Cloud Vision API request.
        val image = Image.newBuilder()
            .setContent(imgProto)
            .build()
        val feature = Feature.newBuilder()
            .setType(Type.LABEL_DETECTION)
            .setType(Type.TEXT_DETECTION)
            .setType(Type.IMAGE_PROPERTIES)
            .build()
        val annotateImageRequest = AnnotateImageRequest.newBuilder()
            .addFeatures(feature)
            .setImage(image)
            .build()

        // Call the Cloud Vision API and perform label detection on the image.
        val requests = arrayListOf(annotateImageRequest)
        val result = imageAnnotatorClient.batchAnnotateImages(requests)!!

        logger.d("Vision AI result: $result")
        // Print the label annotations for the first response.
        result.responsesList[0].labelAnnotationsList.forEach { label ->
            logger.d("${label.description} (${(label.score * 100).toInt()}%)")
        }
        return result.responsesList[0].labelAnnotationsList.first().description ?: "no description"
    }
}
