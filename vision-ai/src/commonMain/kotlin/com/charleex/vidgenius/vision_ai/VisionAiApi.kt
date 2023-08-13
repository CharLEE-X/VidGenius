package com.charleex.vidgenius.vision_ai

import co.touchlab.kermit.Logger
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.protobuf.ByteString
import java.io.File

interface VisionAiApi {
    fun fetchTextFromImage(imageFile: File): Map<Float, String>
}

class VisionAiApiImpl(
    private val logger: Logger,
) : VisionAiApi {
    override fun fetchTextFromImage(imageFile: File): Map<Float, String> {
        val inputStream = this::class.java
            .getResourceAsStream("/vision_ai.json")
            ?: error("Could not find vision_ai.json")

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
            .setType(Type.WEB_DETECTION)
            .build()

        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(feature)
            .setImage(image)
            .build()

        val result: BatchAnnotateImagesResponse = client.batchAnnotateImages(arrayListOf(request))
        logger.d("Vision AI result: $result")

        val annotateImageResponse: AnnotateImageResponse = result.responsesList[0]
        println("Vision AI annotateImageResponse: $annotateImageResponse")

        val webResults = annotateImageResponse.webDetection.webEntitiesList
            .associate { it.score to it.description }
        logger.i { "Vision AI results: $webResults" }

        logger.d("Vision AI:\nimage: $imageFile\ndescription: $webResults")
        return webResults
    }
}
