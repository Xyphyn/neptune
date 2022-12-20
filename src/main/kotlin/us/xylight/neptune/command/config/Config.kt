package us.xylight.neptune.command.config

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.Subcommand

object Config : Command {
    override val name = "config"
    override val description = "General configuration."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(TranslationConfig, ModerationConfig)
    override val permission = Permission.ADMINISTRATOR

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.subcommandName]?.execute(interaction)
    }
}