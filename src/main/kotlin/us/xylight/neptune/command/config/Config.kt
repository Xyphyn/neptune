package us.xylight.neptune.command.config

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.RatelimitedCommand
import us.xylight.neptune.command.Subcommand

object Config : RatelimitedCommand {
    override val name = "config"
    override val description = "General configuration."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(TranslationConfig, ModerationConfig)
    override val permission = Permission.ADMINISTRATOR

    override val cooldown = 15_000L

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.subcommandName]?.execute(interaction)
    }
}