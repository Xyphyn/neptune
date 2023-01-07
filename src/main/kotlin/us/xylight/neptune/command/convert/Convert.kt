package us.xylight.neptune.command.convert

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.Subcommand

object Convert : Command {
    override val name = "convert"
    override val description = "Converts certain units."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(
        Temperature,
        Length,
        Weight,
        Time
    )
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.subcommandName]?.execute(interaction)
    }
}