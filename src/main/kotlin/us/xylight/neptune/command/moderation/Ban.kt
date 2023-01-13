package us.xylight.neptune.command.moderation

import dev.minn.jda.ktx.events.awaitButton
import dev.minn.jda.ktx.interactions.components.secondary
import dev.minn.jda.ktx.messages.Embed
import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.ButtonUtil
import us.xylight.neptune.util.EmbedUtil
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes


object Ban : Subcommand {
    override val name = "ban"
    override val description = "Bans a user from the guild."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "Who should be banned?", true),
        OptionData(OptionType.STRING, "reason", "Why are you banning them?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.getOption("user")?.asMember!!
        val reason = interaction.getOption("reason")?.asString ?: "No reason provided."

        if (!interaction.member!!.canInteract(user)) {
            val embed = EmbedUtil.simpleEmbed(
                "Error",
                "${Config.conf.emoji.uac} You are unable to interact with ${user.asMention}. Do they have a higher permission than you?"
            )

            interaction.hook.editOriginalEmbeds(embed.build()).queue()

            return
        }

        val embed = Moderation.punishEmbed("Ban", "was banned.", reason, Config.conf.emoji.ban, user.user)

        embed.setColor(Config.conf.misc.error)

        val btn = secondary(
            "svy:moderation:ban:undo:${interaction.id}",
            "Undo",
            Emoji.fromFormatted(Config.conf.emoji.trash),
            false
        )

        GlobalScope.launch {
            withTimeoutOrNull(1.minutes) {
                interaction.user.awaitButton(btn)

                interaction.guild?.unban(user)?.queue()

                interaction.hook.retrieveOriginal().queue {
                    it.editMessageComponents(ButtonUtil.disableButtons(it.buttons)).queue()
                }
            } ?: interaction.hook.retrieveOriginal().queue {
                it.editMessageComponents(ButtonUtil.disableButtons(it.buttons)).queue()
            }
        }

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        if (!interaction.guild!!.selfMember.canInteract(user)) {
            interaction.replyEmbeds(Embed {
                title = "Error"
                description =
                    "${Config.conf.emoji.uac} Unable to ban that user. Do they have a higher permission than Neptune?"
                color = Config.conf.misc.error
            }).queue()

            return
        }

        Moderation.notifyUser(user.user, embed)

        user.ban(0, TimeUnit.MILLISECONDS).queue()

        interaction.replyEmbeds(embed.build()).setActionRow(btn).queue()
    }

}