package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.config.Config
import us.xylight.neptune.command.config.Config.get
import us.xylight.neptune.util.EmbedUtil

object Moderation : Command {
    override val name = "mod"
    override val description = "Moderation commands"
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = listOf(
        Mute,
        Warn,
        DeleteWarning,
        Unmute,
        Ban,
    )
    override val permission = Permission.MODERATE_MEMBERS

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.name]?.execute(interaction)
    }

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

        reason?.let {
            embed.addField("Reason", it, false)
        }

        return embed
    }

}