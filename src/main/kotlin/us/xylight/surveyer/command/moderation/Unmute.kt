package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.ocpsoft.prettytime.PrettyTime
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.util.DateParser
import java.util.*
import java.util.concurrent.TimeUnit

class Unmute : Subcommand {
    override val name = "unmute"
    override val description = "Unmutes/removes timeout of a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "The user to unmute.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.getOption("user")!!

        user.asMember?.removeTimeout()?.queue()

        val embed = Moderation.punishEmbed(
            "Unmute",
            "was unmuted.",
            null,
            Config.warningIcon,
            user.asUser
        )

        interaction.reply("").setEmbeds(embed.build()).queue()

        Moderation.notifyUser(user.asUser, embed)
    }
}