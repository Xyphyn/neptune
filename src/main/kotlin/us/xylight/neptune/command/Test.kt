package us.xylight.neptune.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.config.Config

class Test : Command {
    override val name = "test"
    override val description = "test"
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf()
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.reply(Config.getConfig(interaction.guild!!.idLong).toString()).queue()
    }

}