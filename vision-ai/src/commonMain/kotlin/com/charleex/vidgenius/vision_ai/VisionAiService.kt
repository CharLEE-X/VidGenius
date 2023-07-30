package com.charleex.vidgenius.vision_ai

import co.touchlab.kermit.Logger
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.protobuf.ByteString
import java.io.File


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

        val description = quickstart(imageFile)
        logger.d("Vision AI:\nimage: $imageFile\ndescription: $description")
        return description
    }

    private fun quickstart(imageFile: File): String {
        val inputStream = this::class.java
            .getResourceAsStream("/autovidyt-9142f31d5049.json")

        val credentials = GoogleCredentials.fromStream(inputStream)
        val settings = ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()
        val client = ImageAnnotatorClient.create(settings)

        val imgProto = ByteString.copyFrom(imageFile.readBytes())
        val image = Image.newBuilder()
            .setContent(imgProto)
            .build()
        val feature = Feature.newBuilder()
            .setType(Type.LABEL_DETECTION)
            .setType(Type.FACE_DETECTION)
            .setType(Type.LANDMARK_DETECTION)
            .setType(Type.LOGO_DETECTION)
            .setType(Type.OBJECT_LOCALIZATION)
            .setType(Type.PRODUCT_SEARCH)
            .setType(Type.WEB_DETECTION)
            .build()
        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(feature)
            .setImage(image)
            .build()

        // Call the Cloud Vision API and perform label detection on the image.
        val result: BatchAnnotateImagesResponse = client.batchAnnotateImages(arrayListOf(request))
        logger.d("Vision AI result: $result")

        val bestResults = result.responsesList[0].webDetection.webEntitiesList
            .take(3)
            .filter { it.score > 0.45 }
            .joinToString(separator = " ") { it.description }
        logger.i { "Vision AI best results: $bestResults" }

        return bestResults
    }
}
