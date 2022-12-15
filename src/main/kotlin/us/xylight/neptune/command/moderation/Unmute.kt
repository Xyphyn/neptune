package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config

object Unmute : Subcommand {
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
            Config.successIcon,
            user.asUser
        )

        interaction.reply("").setEmbeds(embed.build()).queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)
    }
}