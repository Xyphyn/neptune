package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.handler.CommandHandler

class Moderation : Command {
    override val name = "mod"
    override val description = "Moderation commands"
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = listOf(
        Mute()
    )

    override fun execute(interaction: SlashCommandInteractionEvent) {
        CommandHandler.subcommandFromName(subcommands, interaction.subcommandName!!)?.execute(interaction)
    }

}