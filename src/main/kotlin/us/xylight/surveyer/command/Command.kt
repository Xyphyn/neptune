package us.xylight.surveyer.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface Command {
    val name: String
    val description: String
    val options: List<OptionData>
    val subcommands: List<SubcommandData>

    fun execute(interaction: SlashCommandInteractionEvent)
}