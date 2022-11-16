package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.DatabaseHandler
import us.xylight.surveyer.database.dataclass.Warning
import java.time.Instant
import java.util.Date

class Warn(private val db: DatabaseHandler) : Subcommand {
    override val name = "warn"
    override val description = "Warns a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "The user to warn.", true),
        OptionData(OptionType.STRING, "reason", "Why are they being warned?", false),
        OptionData(OptionType.BOOLEAN, "silent", "Should the warning message be public?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        val user = interaction.getOption("user")!!
        val reason = interaction.getOption("reason")?.asString ?: "No reason provided."
        val silent = interaction.getOption("silent")?.asBoolean ?: false

        db.warnings.insertOne(Warning(
            interaction.guild!!.id,
            user.asUser.id,
            reason,
            interaction.user.id,
            Instant.now().epochSecond,
            db.getAvailableId()
        ))

        val embed =
            Moderation.punishEmbed("Warning", "was warned.", reason, Config.warningIcon, user.asUser)

        interaction.hook.sendMessage("").setEmbeds(embed.build()).setEphemeral(silent).queue()

        Moderation.notifyUser(user.asUser, embed)
    }
}