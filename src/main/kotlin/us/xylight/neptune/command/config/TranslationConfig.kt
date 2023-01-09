package us.xylight.neptune.command.config

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object TranslationConfig : Subcommand {
    override val name = "translation"
    override val description = "Translation configuration."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.BOOLEAN, "reaction", "Allows translation by reacting with a flag.", false),
        OptionData(OptionType.BOOLEAN, "enabled", "If translation should be enabled.", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val reaction = interaction.getOption("reaction")?.asBoolean
        val enabled = interaction.getOption("enabled")?.asBoolean

        interaction.deferReply().setEphemeral(true).queue()

        val currentConfig = Config.getConfig(interaction.guild!!.id.toLong())!!

        if (reaction != null) {
            currentConfig.translation.reactions = reaction
        }

        if (enabled != null) {
            currentConfig.translation.enabled = enabled
        }

        DatabaseHandler.replaceConfig(interaction.guild!!.id.toLong(), currentConfig)

        interaction.hook.sendMessage("").setEmbeds(
            EmbedUtil.simpleEmbed(
                "Set",
                "${Config.conf.emoji.success} Configuration updated."
            ).build()
        ).queue()
    }

}