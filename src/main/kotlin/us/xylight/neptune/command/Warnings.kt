package us.xylight.neptune.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.litote.kmongo.eq
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Warning

object Warnings : Command {
    override val name = "warnings"
    override val description = "Get warnings for a user."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.USER, "member", "The member to get warnings from.", true),
        OptionData(OptionType.BOOLEAN, "id", "Show IDs?", false)
    )
    override val subcommands: List<Subcommand> = listOf()
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        val id = interaction.getOption("id")?.asBoolean ?: false

        val member = interaction.getOption("member")!!.asUser
        val guild = interaction.guild!!

        val warnings = DatabaseHandler.warnings!!.find(
            Warning::user eq member.id,
            Warning::guild eq guild.id
        )

        val embed: EmbedBuilder = EmbedBuilder()
            .setTitle("Warnings")
            .setDescription(if (warnings.toList().isEmpty()) "No warnings found." else "")
            .setColor(Config.conf.misc.accent)

        warnings.toList().reversed().forEach { warn ->
            embed.appendDescription(
                "**${warn.reason}**: <t:${warn.time}:R> ${
                    if (id) "`${warn.id}`" else ""
                }\n"
            )
        }

        interaction.hook.sendMessage("").setEmbeds(embed.build()).queue()
    }

}