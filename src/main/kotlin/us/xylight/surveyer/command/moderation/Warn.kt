package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import us.xylight.surveyer.command.ComponentSubcommand
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.DatabaseHandler
import us.xylight.surveyer.database.dataclass.Warning
import java.time.Instant
import java.util.Date

class Warn(private val db: DatabaseHandler) : ComponentSubcommand {
    override val name = "warn"
    override val description = "Warns a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "The user to warn.", true),
        OptionData(OptionType.STRING, "reason", "Why are they being warned?", false),
        OptionData(OptionType.BOOLEAN, "silent", "Should the warning message be public?", false)
    )
    override val handles: MutableList<Button> = mutableListOf()
    var interactionAuthor: User? = null
    var recentId: Long? = null

    override suspend fun onButtonClick(interaction: ButtonInteractionEvent) {
        if (interactionAuthor != interaction.user) {
            interaction.reply("That button is not yours.").setEphemeral(true).queue()
            return
        }

        if (recentId == null) return
        DeleteWarning(db).execute(interaction, recentId!!)
    }

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        val user = interaction.getOption("user")!!
        val reason = interaction.getOption("reason")?.asString ?: "No reason provided."
        val silent = interaction.getOption("silent")?.asBoolean ?: false
        val id = db.getAvailableId()


        db.warnings.insertOne(Warning(
            interaction.guild!!.id,
            user.asUser.id,
            reason,
            interaction.user.id,
            Instant.now().epochSecond,
            id
        ))

        recentId = id

        val embed =
            Moderation.punishEmbed("Warning", "was warned.", reason, Config.warningIcon, user.asUser)

        embed.setColor(0xfdd100)

        handles.add(0, Button.of(ButtonStyle.SECONDARY, "svy-undowarn", "Undo", Emoji.fromFormatted(Config.trashIcon)))

        interaction.hook.sendMessage("").setEmbeds(embed.build())
            .setActionRow(handles)
            .setEphemeral(silent)
            .queue()

        interactionAuthor = interaction.user

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)
    }
}