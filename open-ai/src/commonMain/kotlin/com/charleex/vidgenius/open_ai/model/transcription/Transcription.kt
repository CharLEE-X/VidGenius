package com.charleex.vidgenius.open_ai.model.transcription

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transcription(
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,
)

/**
 * Example response:
 *
 * Transcription(
 *   text=Cześć, jak się masz, jaka jest pogoda dzisiaj?,
 *   language=polish,
 *   duration=5.54,
 *   segments=[
 *     Segment(
 *       id=0,
 *       seek=0,
 *       start=0.0,
 *       end=4.0,
 *       text= Cześć, jak się masz, jaka jest pogoda dzisiaj?,
 *       tokens=[50364, 383, 1381, 7753, 11, 4207, 3244, 2300, 89, 11, 4207, 64, 3492, 32037, 13449, 25772, 30, 50564],
 *       temperature=0.0,
 *       avgLogprob=-0.3027102068850869,
 *       compressionRatio=0.9074074074074074,
 *       noSpeechProb=0.02790289744734764,
 *       transient=null
 *     )
 *   ]
 * )
 */
