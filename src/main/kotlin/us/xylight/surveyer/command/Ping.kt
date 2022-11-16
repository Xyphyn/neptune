package us.xylight.surveyer.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.util.EmbedUtil
import java.util.Collections

class Ping : Command {
    override val name = "ping"
    override val description = "Returns Discord API ping."
    override val options: List<OptionData> = listOf(OptionData(OptionType.BOOLEAN, "ms",
        "Should the value be in ms? (Default: true)", false))
    override val subcommands: List<Subcommand> = emptyList()
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val time = System.currentTimeMillis()

        interaction.reply("").setEmbeds(EmbedUtil.simpleEmbed("Pong!", "Calculating ping...").build()).flatMap { v ->
            v.editOriginalEmbeds(
                EmbedUtil.simpleEmbed(
                    "Pong!",
                    String.format("%s Test Ping: %d", Config.wifiIcon, (System.currentTimeMillis() - time))
                ).build()
            )
        }.queue()
    }
}