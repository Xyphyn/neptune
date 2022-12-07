package us.xylight.neptune.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

interface Subcommand {
    // This is so that a separate class can be a subcommand,
    // rather than there being an endless chain of
    // SubcommandData()

    val name: String
    val description: String
    val options: List<OptionData>

    suspend fun execute(interaction: SlashCommandInteractionEvent)
}