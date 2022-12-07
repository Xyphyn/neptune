package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.ocpsoft.prettytime.PrettyTime
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.DateParser
import us.xylight.neptune.util.EmbedUtil
import java.util.*
import java.util.concurrent.TimeUnit

class Mute : Subcommand {
    override val name = "mute"
    override val description = "Times out a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "The user to mute/timeout.", true),
        OptionData(OptionType.STRING, "time", "How long they should be muted. time[s, m, h, d]", true),
        OptionData(OptionType.STRING, "reason", "Why are they being muted?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.getOption("user")!!
        val time = interaction.getOption("time")!!
        val reason = interaction.getOption("reason")?.asString ?: "No reason provided."

        val millis = DateParser.millisFromTime(time.asString)

        user.asMember?.timeoutFor(millis, TimeUnit.MILLISECONDS)?.queue()

        val p = PrettyTime(Locale.ENGLISH)
        val formatted = p.formatDurationUnrounded(Date(System.currentTimeMillis() + millis))

        val embed = Moderation.punishEmbed("Timeout", "was muted for ${if (formatted == "") "${millis / 1000} seconds" else formatted}", reason, Config.muteIcon, user.asUser)
        embed.setColor(0xfdd100)

        interaction.reply("").setEmbeds(embed.build()).queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)
    }

}