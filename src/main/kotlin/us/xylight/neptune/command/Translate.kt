package us.xylight.neptune.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil
import kotlin.math.roundToInt

class Translate : Command {
    private val choices: List<Choice> = listOf(
        Choice("English", "en"),
        Choice("Spanish", "es"),
        Choice("Hebrew", "he"),
        Choice("Japanese", "ja"),
        Choice("Chinese", "zh"),
        Choice("French", "fr"),
        Choice("German", "de"),
        Choice("Italian", "it"),
        Choice("Russian", "ru")
    )

    override val name = "translate"
    override val description = "Translates any text to any language!"
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "text", "The text to translate.", true).setMaxLength(1000),
        OptionData(OptionType.STRING, "language", "The language to translate to.", true).addChoices(
            choices
        ),
        OptionData(
            OptionType.STRING,
            "from",
            "The language to translate from. Use this if the auto-detection doesn't work.",
            false
        ).addChoices(
            choices
        ),
        OptionData(OptionType.BOOLEAN, "silent", "Makes the translation only visible to you.", false)
    )
    override val subcommands: List<Subcommand> = listOf()
    override val permission = null

    @Serializable
    data class TranslationRequest(
        @SerialName("q") val text: String,
        val source: String,
        val target: String,
        val format: String = "text",
        @SerialName("api_key") val apiKey: String
    )

    @Serializable
    data class TranslationResponse(
        val detectedLanguage: DetectedLanguage? = null,
        val translatedText: String
    )

    @Serializable
    data class DetectedLanguage(val confidence: Float, val language: String)

    private val client = CommandHandler.httpClient

    private val langNames: Map<String, String> = mapOf(
        "en" to "\uD83C\uDDEC\uD83C\uDDE7 English",
        "es" to "\uD83C\uDDEA\uD83C\uDDF8 Spanish",
        "he" to "\uD83C\uDDEE\uD83C\uDDF1 Hebrew",
        "ja" to "\uD83C\uDDEF\uD83C\uDDF5 Japanese",
        "zh" to "\uD83C\uDDE8\uD83C\uDDF3 Chinese",
        "fr" to "\uD83C\uDDEB\uD83C\uDDF7 French",
        "de" to "\uD83C\uDDE9\uD83C\uDDEA German",
        "it" to "\uD83C\uDDEE\uD83C\uDDF9 Italian",
        "ru" to "\uD83C\uDDF7\uD83C\uDDFA Russian"
    )

    private fun fetchTranslation(text: String, lang: String, from: String = "auto"): TranslationResponse {
        val jsonPayload = Json.encodeToJsonElement(TranslationRequest(text, from, lang, "text", ""))

        val request = Request.Builder()
            .method("POST", jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .url(CommandHandler.translateServer)
            .build()

        client.newCall(request).execute().use { res ->
            val resText = res.body?.string()!!
            val translation = Json.decodeFromString<TranslationResponse>(resText)

            res.body?.close()

            return translation
        }
    }


    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val text = interaction.getOption("text")!!
        val lang = interaction.getOption("language")!!
        val from = interaction.getOption("from")?.asString ?: "auto"
        val silent = interaction.getOption("silent")?.asBoolean ?: false

        if (Config.getConfig(interaction.guild!!.idLong)?.translation?.enabled == false) {
            interaction.reply("").setEphemeral(true).setEmbeds(
                EmbedUtil.simpleEmbed("Disabled", "Translation is not enabled in this server.", 0xff0f0f).build()
            ).queue()

            return
        }

        val embed =
            EmbedUtil.simpleEmbed("Translation", "")
                .addField("Input", text.asString, false)
                .addField("Translated", Config.loadIcon, false)
                .setFooter("to ${langNames[lang.asString]}")

        interaction.reply("").setEmbeds(embed.build()).setEphemeral(silent).queue()

        val translation = fetchTranslation(text.asString, lang.asString, from)

        val confidence = translation.detectedLanguage?.confidence?.roundToInt()
        val stringConfidence =
            if (confidence == null || confidence == 0) "${langNames[from] ?: "Unknown"} " else "${langNames[translation.detectedLanguage.language]} [${confidence}%] "




        embed.clearFields()
        embed
            .addField("Input", text.asString, false)
            .addField("Translated", translation.translatedText, false)
            .setFooter("${stringConfidence}to ${langNames[lang.asString]}")

        if (confidence != null && (confidence <= 15 && from == "auto")) {
            embed.addField(
                "Notice",
                "The language autodetection couldn't accurately detect the input language. Use the 'from' parameter in the </translate:0> command to get a more accurate translation.",
                false
            )
        }

        interaction.hook.editOriginalEmbeds(
            embed.build()
        ).queue()
    }

    fun execute(message: Message, text: String, lang: String, user: User) {
        if (message.contentRaw.length > 1000) return
        val reply = message.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Translation", "")
                .addField("Input", message.contentRaw, false)
                .addField("Translated", Config.loadIcon, false)
                .setFooter("to ${langNames[lang]}")
                .build()
        ).complete()

        val translation = fetchTranslation(text, lang)

        val confidence = translation.detectedLanguage?.confidence?.roundToInt()
        val stringConfidence =
            if (confidence == null) "" else "${langNames[translation.detectedLanguage.language]} [${confidence}%] "

        val embed =
            EmbedUtil.simpleEmbed("Translation", "")
                .addField("Input", text, false)
                .addField("Translated", translation.translatedText, false)
                .setFooter("${stringConfidence}to ${langNames[lang]} • Called by ${user.name}")

        if (confidence != null && confidence <= 15) {
            embed.addField(
                "Notice",
                "The language autodetection couldn't accurately detect the input language. Use the 'from' parameter in the </translate:0> command to get a more accurate translation.",
                false
            )
        }

        reply.editMessageEmbeds(
            embed.build()
        )
            .queue()
    }
}