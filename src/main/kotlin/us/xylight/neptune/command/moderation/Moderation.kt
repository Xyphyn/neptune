package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.ComponentCommand
import us.xylight.neptune.command.ComponentSubcommand
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.handler.CommandHandler
import us.xylight.neptune.util.EmbedUtil

class Moderation(commandHandler: CommandHandler) : Command {
    override val name = "mod"
    override val description = "Moderation commands"
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = listOf(
        Mute(),
        Warn(commandHandler),
        DeleteWarning(),
        Unmute(),
        Ban(),
        Config()
    )
    override val permission = Permission.MODERATE_MEMBERS

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        CommandHandler.subcommandFromName(subcommands, interaction.subcommandName!!)?.execute(interaction)
    }

    companion object {
        fun notifyUser(user: User, embed: EmbedBuilder) {
            user.openPrivateChannel().flatMap { channel ->
                channel.sendMessage("").setEmbeds(
                    embed.build()
                )
            }.queue()
        }

        fun punishEmbed(title: String, action: String, reason: String?, icon: String, user: User): EmbedBuilder {
            val embed = EmbedUtil.simpleEmbed(
                title,
                "$icon ${user.asMention} $action"
            )
            if (reason != null) {
                embed.addField("Reason", reason, false)
            }

            return embed
        }
    }
}