package us.xylight.neptune.command.`fun`

import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil

class WhoAsked : Subcommand {
    override val name = "whoasked"
    override val description = "Who asked?"
    override val options: List<OptionData> = listOf()
    private val lines = listOf("Connecting to haxx0r database...", "Scanning multiverse...", "Hacking Google to find data...", "Searching public records...", "Asking God...")


    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val embed = EmbedUtil.simpleEmbed("Finding who asked...", "")
        interaction.reply("").setEmbeds(embed.build()).queue()
        val desc = mutableListOf<String>()
        repeat(lines.size) {
            i ->
            desc.add("${Config.loadIcon} ${lines[i]}")
            embed.setDescription(desc.joinToString("\n\n"))
            interaction.hook.editOriginalEmbeds(embed.build()).queue()
            delay(3000)
            desc[i] = "${Config.successIcon} ${lines[i]}"
        }
        desc.add("${Config.banIcon} Error: **Failed to find who asked.**")

        embed.setDescription(desc.joinToString("\n\n"))
        interaction.hook.editOriginalEmbeds(embed.build()).queue()
    }
}