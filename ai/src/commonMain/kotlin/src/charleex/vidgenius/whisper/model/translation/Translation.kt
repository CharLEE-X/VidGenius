package src.charleex.vidgenius.whisper.model.translation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,

    // Commented out what we are not using for now
//    @SerialName("duration") val duration: Double? = null,
//    @SerialName("segments") val segments: List<Segment>? = null,
)

/**
 * Example response:
 *
 * Translation(
 *   text=Hi, how are you? What's the weather like today?,
 *   language=english,
 *   duration=5.54,
 *   segments=[
 *     Segment(
 *       id=0,
 *       seek=0,
 *       start=0.0,
 *       end=4.0,
 *       text= Hi, how are you? What's the weather like today?,
 *       tokens=[50414, 2421, 11, 577, 366, 291, 30, 708, 311, 264, 5503, 411, 965, 30, 50564],
 *       temperature=0.0,
 *       avgLogprob=-0.4509975016117096,
 *       compressionRatio=0.8867924528301887,
 *       noSpeechProb=0.108597531914711,
 *       transient=null
 *     )
 *   ]
 * )
 */
