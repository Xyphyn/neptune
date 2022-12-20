package us.xylight.neptune.command.moderation

import dev.minn.jda.ktx.interactions.components.button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.event.Interaction
import us.xylight.neptune.util.EmbedUtil
import java.util.concurrent.TimeUnit
import kotlin.time.Duration


object Ban : Subcommand {
    override val name = "ban"
    override val description = "Bans a user from the guild."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "Who should be banned?", true),
        OptionData(OptionType.STRING, "reason", "Why are you banning them?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.getOption("user")!!
        val reason = interaction.getOption("reason")?.asString ?: "No reason provided."

        val embed = Moderation.punishEmbed("Ban", "was banned.", reason, Config.banIcon, user.asUser)

        embed.setColor(0xff0f0f)

        val btn = interaction.jda.button(
            ButtonStyle.SECONDARY,
            "Undo",
            Emoji.fromFormatted(Config.trashIcon),
            false,
            Duration.parse("60s"),
            interaction.user
        ) {
            interaction.hook.retrieveOriginal().queue {
                message ->
                message.editMessage("").setActionRow(message.buttons[0].asDisabled())
            }

            interaction.guild?.unban(user.asUser)?.queue()
        }

        interaction.reply("").setEmbeds(embed.build()).setActionRow(btn).queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)

        user.asMember?.ban(0, TimeUnit.MILLISECONDS)?.queue()
    }

}