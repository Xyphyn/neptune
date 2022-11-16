package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.DatabaseHandler
import us.xylight.surveyer.handler.CommandHandler
import us.xylight.surveyer.util.EmbedUtil

class Moderation(val db: DatabaseHandler) : Command {
    override val name = "mod"
    override val description = "Moderation commands"
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = listOf(
        Mute(),
        Warn(db),
        DeleteWarning(db),
        Unmute()
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