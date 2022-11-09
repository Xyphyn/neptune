package us.xylight.surveyer.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

interface Command {
    val name: String
    val description: String
    val options: List<OptionData>
    val id: Number?

    fun execute(interaction: SlashCommandInteractionEvent)
}