package us.xylight.neptune.command.moderation

import dev.minn.jda.ktx.interactions.components.button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Warning
import us.xylight.neptune.event.Interaction
import us.xylight.neptune.util.EmbedUtil
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit

object Warn : Subcommand {
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
        val id = DatabaseHandler.getAvailableWarningId()

        if (!interaction.member!!.canInteract(user.asMember!!)) {
            val embed = EmbedUtil.simpleEmbed("Error", "${Config.conf.emoji.uac} You are unable to interact with ${user.asUser.asMention}. Do they have a higher permission than you?")

            interaction.hook.editOriginalEmbeds(embed.build()).queue()

            return
        }

        DatabaseHandler.warnings!!.insertOne(
            Warning(
                interaction.guild!!.id,
                user.asUser.id,
                reason,
                interaction.user.id,
                Instant.now().epochSecond,
                id
            )
        )

        val embed =
            Moderation.punishEmbed("Warning", "was warned.", reason, Config.conf.emoji.warning, user.asUser)

        embed.setColor(0xfdd100)

        val btn = interaction.jda.button(
            ButtonStyle.SECONDARY,
            "Undo",
            Emoji.fromFormatted(Config.conf.emoji.trash),
            false,
            Duration.parse("60s"),
            interaction.user
        ) {
            interaction.hook.retrieveOriginal().queue {
                message ->
                interaction.hook.editOriginal("").setActionRow(message.buttons[0].asDisabled()).queue()
            }

            DeleteWarning.execute(it, id)
        }

        interaction.hook.sendMessage("").setEmbeds(embed.build())
            .setActionRow(btn)
            .setEphemeral(silent)
            .queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)

        val config = Config.getConfig(interaction.guild!!.idLong)!!
        val warnings =
            DatabaseHandler.warnings!!.find(
                Warning::guild eq interaction.guild?.id,
                Warning::user eq user.asUser.id,
                Warning::time gt (System.currentTimeMillis() / 1000) - 259200
            )

        if (warnings.toList().size >= config.moderation.warningThresh) {
            user.asMember?.timeoutFor(3, TimeUnit.HOURS)?.queue()

            val notification = Moderation.punishEmbed(
                "Timeout",
                "was muted for 3 hours",
                "Automatic mute after ${config.moderation.warningThresh} warnings within 72 hours.",
                Config.conf.emoji.mute,
                user.asUser
            )

            notification.setColor(0xfdd100)

            interaction.channel.sendMessage("").setEmbeds(notification.build()).queue()
            Moderation.notifyUser(user.asUser, notification)
        }

    }
}