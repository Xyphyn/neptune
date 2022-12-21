package us.xylight.neptune.command.translate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeepLTranslationRequest(
    val text: List<String>,
    @SerialName("source_lang") val source: String?,
    @SerialName("target_lang") val target: String,
)

@Serializable
data class DeepLTranslationResponse(
    val translations: List<DeepLTranslation>
)

@Serializable
data class DeepLTranslation(
    @SerialName("detected_source_language") val detectedSourceLanguage: String,
    @SerialName("text") val translatedText: String
)