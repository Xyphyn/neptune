package us.xylight.surveyer.command.time

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.util.EmbedUtil

class Current : Subcommand {
    override val name = "current"
    override val description = "Gets the current time (PST)."
    override val options: List<OptionData> = listOf()

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val embed = EmbedUtil.simpleEmbed(" ", "")
            .addField("Time", "<t:${System.currentTimeMillis() / 1000}:f>", false)
            .addField("Epoch", "`${System.currentTimeMillis() / 1000}`", false)

        interaction.reply("").setEmbeds(embed.build()).queue()
    }
}