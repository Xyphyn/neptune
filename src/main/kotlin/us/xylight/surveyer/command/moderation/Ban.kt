package us.xylight.surveyer.command.moderation

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
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.event.Interaction
import java.util.concurrent.TimeUnit


class Ban : Subcommand {
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

        val btn = Button.of(ButtonStyle.SECONDARY, "moderation:ban:undo:${interaction.id}", "Undo", Emoji.fromFormatted(Config.trashIcon))

        CoroutineScope(Dispatchers.Default).launch {
            delay(TimeUnit.SECONDS.toMillis(10))
            runCatching {
                Interaction.unSubscribe(btn, interaction.hook.retrieveOriginal().complete())
            }
        }

        Interaction.subscribe(btn.id!!) lambda@ { btnInter ->
            if (btnInter.user != interaction.user) {
                btnInter.reply("That button is not yours.").setEphemeral(true).queue()
                return@lambda false
            }

            interaction.guild?.unban(user.asUser)?.queue()

            return@lambda true
        }

        interaction.reply("").setEmbeds(embed.build()).setActionRow(btn).queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)

        user.asMember?.ban(0, TimeUnit.MILLISECONDS)?.queue()
    }

}