package us.xylight.neptune.command.config

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object ModerationConfig : Subcommand {
    override val name = "moderation"
    override val description = "Moderation configuration."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "warnthreshold", "How many warnings before a user is timed out?", false),
        OptionData(OptionType.CHANNEL, "logchannel", "Where to log moderation actions.", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().setEphemeral(true).queue()

        val warnThresh = interaction.getOption("warnthreshold")
        val logChannel = interaction.getOption("logchannel")

        val currentConfig = Config.getConfig(interaction.guild!!.id.toLong())!!

        if (warnThresh != null) {
            currentConfig.moderation.warningThresh = warnThresh.asInt
        }

        if (logChannel != null) {
            currentConfig.moderation.modlogChannel = logChannel.asChannel.id.toLong()
        }

        DatabaseHandler.replaceConfig(interaction.guild!!.id.toLong(), currentConfig)

        interaction.hook.sendMessage("").setEmbeds(
            EmbedUtil.simpleEmbed(
                "Set",
                "${Config.conf.emoji.success} Configuration updated.",
                Config.conf.misc.success
            ).build()
        ).queue()
    }
}