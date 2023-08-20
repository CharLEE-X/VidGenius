package com.charleex.vidgenius.open_ai.model.transcription

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Segment(
    @SerialName("id") val id: Int,
    @SerialName("seek") val seek: Int = 0,
    @SerialName("start") val start: Double = 0.0,
    @SerialName("end") val end: Double = 0.0,
    @SerialName("text") val text: String = "",
    @SerialName("tokens") val tokens: List<Int> = emptyList(),
    @SerialName("temperature") val temperature: Double = 0.0,
    @SerialName("avg_logprob") val avgLogprob: Double = 0.0,
    @SerialName("compression_ratio") val compressionRatio: Double = 0.0,
    @SerialName("no_speech_prob") val noSpeechProb: Double = 0.0,
    @SerialName("transient") val transient: Boolean? = null,
)

/**
 * Example response:
 *
 * Segment(
 *   id=0,
 *   seek=0,
 *   start=0.0,
 *   end=4.0,
 *   text= Hi, how are you? What's the weather like today?,
 *   tokens=[50414, 2421, 11, 577, 366, 291, 30, 708, 311, 264, 5503, 411, 965, 30, 50564],
 *   temperature=0.0,
 *   avgLogprob=-0.4509975016117096,
 *   compressionRatio=0.8867924528301887,
 *   noSpeechProb=0.108597531914711,
 *   transient=null
 * )
 */
