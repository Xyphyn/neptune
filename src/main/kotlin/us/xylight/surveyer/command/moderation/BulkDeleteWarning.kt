package us.xylight.surveyer.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import org.litote.kmongo.eq
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.DatabaseHandler
import us.xylight.surveyer.database.dataclass.Warning
import us.xylight.surveyer.util.EmbedUtil

class BulkDeleteWarning(private val db: DatabaseHandler) : Subcommand {
    override val name = "delwarn"
    override val description = "Deletes a warning."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "Warning ID to delete. (Can be found in /warnings)", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()
        val id = interaction.getOption("id")!!

        val history = db.warnings.deleteMany(
            Warning::id eq id.asLong,
            Warning::guild eq interaction.guild!!.id)

        if (history.deletedCount <= 0) {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed("Failed to delete", "${Config.errorIcon} That warning is not from this guild, or it doesn't exist. It was not deleted.", 0xff1f1f).build()
            ).queue()

            return
        }

        val embed = EmbedUtil.simpleEmbed("Warning Deletion", "${Config.trashIcon} Warning of ID `${id.asLong}` has been deleted.")
        interaction.hook.sendMessage("").setEmbeds(embed.build()).queue()
    }
}