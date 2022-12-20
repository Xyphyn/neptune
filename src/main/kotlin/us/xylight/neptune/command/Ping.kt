package us.xylight.neptune.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.util.EmbedUtil
import us.xylight.neptune.config.Config

object Ping : Command {
    override val name = "ping"
    override val description = "Returns Discord API ping."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = emptyList()
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val time = System.currentTimeMillis()
        
        interaction.reply("").setEmbeds(EmbedUtil.simpleEmbed("Pong!", "Calculating ping...").build()).flatMap { v ->
            v.editOriginalEmbeds(
                EmbedUtil.simpleEmbed(
                    "Pong!",
                    "${Config.wifiIcon} Request ping: ${System.currentTimeMillis() - time}ms\nGateway ping: ${interaction.jda.gatewayPing}ms"
                ).build()
            )
        }.queue()
    }
}
