package us.xylight.surveyer.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.util.EmbedUtil

class Ping() : Command {
    override val name = "ping"
    override val description = "Returns Discord API ping."
    override val options: List<OptionData> = listOf(OptionData(OptionType.STRING, "test", "test"))
    override val id: Number? = null

    override fun execute(interaction: SlashCommandInteractionEvent) {
        val time = System.currentTimeMillis()
        interaction.reply("").setEmbeds(EmbedUtil.simpleEmbed("Pong!", "Calculating ping...").build()).flatMap { v ->
            v.editOriginalEmbeds(
                EmbedUtil.simpleEmbed(
                    "Pong!",
                    String.format("Ping: %d ms", System.currentTimeMillis() - time)
                ).build()
            )
        }.queue()
    }
}