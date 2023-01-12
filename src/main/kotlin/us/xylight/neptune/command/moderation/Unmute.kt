package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil

object Unmute : Subcommand {
    override val name = "unmute"
    override val description = "Unmutes/removes timeout of a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "user", "The user to unmute.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val user = interaction.getOption("user")!!

        if (!interaction.member!!.canInteract(user.asMember!!)) {
            val embed = EmbedUtil.simpleEmbed("Error", "${Config.conf.emoji.uac} You are unable to interact with ${user.asUser.asMention}. Do they have a higher permission than you?")

            interaction.replyEmbeds(embed.build()).queue()

            return
        }

        user.asMember?.removeTimeout()?.queue()

        val embed = Moderation.punishEmbed(
            "Unmute",
            "was unmuted.",
            null,
            Config.conf.emoji.success,
            user.asUser
        )

        interaction.reply("").setEmbeds(embed.build()).queue()

        embed.setFooter(interaction.guild?.name, interaction.guild?.iconUrl)

        Moderation.notifyUser(user.asUser, embed)
    }
}