package us.xylight.neptune.command.moderation

import dev.minn.jda.ktx.events.awaitButton
import dev.minn.jda.ktx.interactions.components.secondary
import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import us.xylight.neptune.LogLevel
import us.xylight.neptune.Logger
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Warning
import us.xylight.neptune.util.ButtonUtil
import us.xylight.neptune.util.EmbedUtil
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

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

        val btn = secondary(
            "svy:moderation:warn:undo:${interaction.id}",
            "Undo",
            Emoji.fromFormatted(Config.conf.emoji.trash),
            false,
        )

        GlobalScope.launch {
            withTimeoutOrNull(10.seconds) {
                val event = interaction.user.awaitButton(btn)

                interaction.hook.retrieveOriginal().queue { it.editMessageComponents(ButtonUtil.disableButtons(it.buttons)).queue() }

                DeleteWarning.execute(event.interaction, id)
            } ?: interaction.hook.retrieveOriginal().queue {
                it.editMessageComponents(ButtonUtil.disableButtons(it.buttons)).queue()
            }
        }

        interaction.hook.sendMessageEmbeds(embed.build())
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