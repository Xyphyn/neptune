package us.xylight.neptune.command.roles

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object Delete : Subcommand {
    override val name = "delete"
    override val description = "Deletes a role picker."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to remove.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", 0xff0f0f).build())
                .queue()

            return
        }

        if (selection.guildId != interaction.guild!!.idLong) {

            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "The role picker of that ID does not belong to this guild.", 0xff0f0f).build())
                .queue()

            return
        }

        DatabaseHandler.deleteRoleSelection(selection.id)
        (interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).deleteMessageById(
            selection.msgId
        ).queue()

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Deleted", "Deleted that role picker.").build()).queue()
    }

}