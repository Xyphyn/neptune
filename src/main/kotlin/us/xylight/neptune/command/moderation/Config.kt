package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

class Config : Subcommand {
    override val name = "config"
    override val description = "Moderation settings."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "warnthreshold", "How many warnings before a user is timed out?", false),
        OptionData(OptionType.CHANNEL, "logchannel", "Where to log moderation actions.", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        val warnThresh = interaction.getOption("warnthreshold")
        val logChannel = interaction.getOption("logchannel")

        val currentConfig = us.xylight.neptune.config.Config.getConfig(interaction.guild!!.id.toLong())!!

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
                "${us.xylight.neptune.config.Config.successIcon} Configuration updated."
            ).build()
        ).queue()
    }
}