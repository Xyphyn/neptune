package us.xylight.neptune.command.`fun`

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.ComponentCommand
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.command.CommandHandler

object Fun : ComponentCommand {
    override val name = "fun"
    override val description = "Fun commands"
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(
        Reddit,
        Fact,
    )
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.name]?.execute(interaction)
    }

}