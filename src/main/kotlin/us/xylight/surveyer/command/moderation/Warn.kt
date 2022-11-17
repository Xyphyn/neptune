package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import us.xylight.surveyer.command.ComponentSubcommand
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.DatabaseHandler
import us.xylight.surveyer.database.dataclass.Warning
import us.xylight.surveyer.event.Interaction
import us.xylight.surveyer.handler.CommandHandler
import java.lang.ref.WeakReference
import java.time.Instant
import java.util.Date

class Warn(private val db: DatabaseHandler, private val commandHandler: CommandHandler) : Subcommand {
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
        val id = db.getAvailableId()

        db.warnings.insertOne(
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
            Moderation.punishEmbed("Warning", "was warned.", reason, Config.warningIcon, user.asUser)

        embed.setColor(0xfdd100)

        val btn = Button.of(ButtonStyle.SECONDARY, "svy-undowarn-${interaction.id}", "Undo", Emoji.fromFormatted(Config.trashIcon))
//        println(handles)

        interaction.hook.sendMessage("").setEmbeds(embed.build())
            .setActionRow(btn)
            .setEphemeral(silent)
            .queue()

        Interaction.subscribe(btn.id!!) lambda@ { btnInter ->
            if (btnInter.user != interaction.user) {
                btnInter.reply("That button is not yours.").setEphemeral(true).queue()
                return@lambda false
            }

            (commandHandler.commandClasses.find { command ->
                command is Moderation
            }?.subcommands?.find { subcommand ->
                subcommand is DeleteWarning
            } as DeleteWarning).execute(
                btnInter,
                id
            )

            return@lambda true
        }

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)
    }
}