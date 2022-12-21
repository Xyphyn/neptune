package us.xylight.neptune.command.translate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LibreTranslationRequest(
    @SerialName("q") val text: String,
    val source: String,
    val target: String,
    val format: String = "text",
    @SerialName("api_key") val apiKey: String
)

@Serializable
data class LibreTranslationResponse(
    val detectedLanguage: LibreDetectedLanguage? = null,
    val translatedText: String
)

@Serializable
data class LibreDetectedLanguage(val confidence: Float, val language: String)