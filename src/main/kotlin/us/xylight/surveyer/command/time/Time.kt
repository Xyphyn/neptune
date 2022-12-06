package us.xylight.surveyer.command.time

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.handler.CommandHandler

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