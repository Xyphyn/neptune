package us.xylight.neptune.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

interface Command {
    val name: String
    val description: String
    val options: List<OptionData>
    val subcommands: List<Subcommand>
    val permission: Permission?
    suspend fun execute(interaction: SlashCommandInteractionEvent)
}