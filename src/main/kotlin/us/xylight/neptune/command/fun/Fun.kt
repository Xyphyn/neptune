package us.xylight.neptune.command.`fun`

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import us.xylight.neptune.command.ComponentCommand
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.handler.CommandHandler

class Fun : ComponentCommand {
    override val name = "fun"
    override val description = "Fun commands"
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(
        Reddit(),
        Fact(),
    )
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        CommandHandler.subcommandFromName(subcommands, interaction.subcommandName!!)?.execute(interaction)
    }

}