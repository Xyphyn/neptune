package us.xylight.neptune.command.moderation

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import org.litote.kmongo.eq
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Warning
import us.xylight.neptune.util.EmbedUtil

object DeleteWarning : Subcommand {
    override val name = "delwarn"
    override val description = "Deletes a warning."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "Warning ID to delete. (Can be found in /warnings)", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()
        val id = interaction.getOption("id")!!

        val history = DatabaseHandler.warnings!!.deleteMany(Warning::id eq id.asLong,
            Warning::guild eq interaction.guild!!.id)

        if (history.deletedCount <= 0) {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed("Failed to delete", "${Config.conf.emoji.error} That warning is not from this guild, or it doesn't exist. It was not deleted.", Config.conf.misc.error).build()
            ).queue()

            return
        }

        val embed = EmbedUtil.simpleEmbed("Warning Deletion", "${Config.conf.emoji.trash} Warning of ID `${id.asLong}` has been deleted.")
        interaction.hook.sendMessage("").setEmbeds(embed.build()).queue()
    }

    suspend fun execute(interaction: ButtonInteraction, id: Long) {
        interaction.deferReply().queue()

        val history = DatabaseHandler.warnings!!.deleteMany(Warning::id eq id,
            Warning::guild eq interaction.guild!!.id)

        if (history.deletedCount <= 0) {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed("Failed to delete", "${Config.conf.emoji.error} That warning is not from this guild, or it doesn't exist. It was not deleted.", Config.conf.misc.error).build()
            ).queue()

            return
        }

        val embed = EmbedUtil.simpleEmbed("Warning Deletion", "${Config.conf.emoji.trash} Warning of ID `${id}` has been deleted.")
        interaction.hook.sendMessage("").setEmbeds(embed.build()).queue()
    }
}