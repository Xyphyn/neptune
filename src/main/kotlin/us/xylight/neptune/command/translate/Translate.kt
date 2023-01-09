package us.xylight.neptune.command.translate

import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.multitranslate.Provider
import us.xylight.multitranslate.enums.Language
import us.xylight.multitranslate.translators.Translator
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.RatelimitedCommand
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil

object Translate : RatelimitedCommand {
    private val translator: Translator =
        Translator.Builder().provider(Provider.DEEPL).key(CommandHandler.deeplKey).build()

    private val choices: List<Choice> = listOf(
        Choice("English", "en"),
        Choice("Spanish", "es"),
        Choice("Japanese", "ja"),
        Choice("Chinese", "zh"),
        Choice("French", "fr"),
        Choice("German", "de"),
        Choice("Italian", "it"),
        Choice("Russian", "ru")
    )

    override val name = "translate"
    override val description = "Translates any text."
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

    override val cooldown: Long = 10_000L

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

    private val json = Json {
        encodeDefaults = false
    }

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val text = interaction.getOption("text")!!
        val lang = interaction.getOption("language")!!
        val from = interaction.getOption("from")?.asString
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
                .addField("Translated", Config.conf.emoji.load, false)
                .setFooter("to ${langNames[lang.asString]}")

        interaction.reply("").setEmbeds(embed.build()).setEphemeral(silent).queue()

        val fromLanguage = if (from != null) {
            Language.languageFromCode(from)
        } else null

        val translation = translator.translate(text.asString, Language.languageFromCode(lang.asString)!!, fromLanguage)

        embed.clearFields()
        embed
            .addField("Input", text.asString, false)
            .addField("Translated", translation.translatedText, false)
            .setFooter("${langNames[translation.detectedLanguage?.code?.lowercase()]} to ${langNames[lang.asString]}")

        interaction.hook.editOriginalEmbeds(
            embed.build()
        ).queue()
    }

    suspend fun execute(message: Message, text: String, lang: String, user: User) {
        if (message.contentRaw.length > 1000) return
        val reply = message.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Translation", "")
                .addField("Input", message.contentRaw, false)
                .addField("Translated", Config.conf.emoji.load, false)
                .setFooter("to ${langNames[lang]}")
                .build()
        ).complete()

        val translation = translator.translate(text, Language.languageFromCode(lang)!!, null)

        val embed =
            EmbedUtil.simpleEmbed("Translation", "")
                .addField("Input", text, false)
                .addField("Translated", translation.translatedText, false)
                .setFooter("${langNames[translation.detectedLanguage?.code?.lowercase()]} to ${langNames[lang]} • Called by ${user.name}")

        reply.editMessageEmbeds(
            embed.build()
        ).queue()
    }
}