package us.xylight.neptune.command.time

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.Subcommand

class Time : Command {
    override val name = "time"
    override val description = "Time commands."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(Current())
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        CommandHandler.subcommandFromName(subcommands, interaction.subcommandName!!)?.execute(interaction)
    }
}