package us.xylight.neptune.command.roles

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

class Edit : Subcommand {
    override val name = "edit"
    override val description = "Edits the title/description of a role selection."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to edit.", true),
        OptionData(OptionType.STRING, "newtitle", "The new title for the role picker.", true),
        OptionData(OptionType.STRING, "newdesc", "The new description for the role picker.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong
        val title = interaction.getOption("newtitle")!!.asString
        val desc = interaction.getOption("newdesc")!!.asString

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", 0xff0f0f).build())
                .queue()

            return
        }

        if (selection.guildId != interaction.guild!!.idLong) return

        (interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).editMessageEmbedsById(
            selection.msgId,
            EmbedUtil.simpleEmbed(title, desc).setFooter("ID: ${selection.id}").build()
        ).queue()

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Updated", "Updated that role picker.").build()).queue()
    }

}