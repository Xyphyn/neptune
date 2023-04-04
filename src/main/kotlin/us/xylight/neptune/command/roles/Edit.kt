package us.xylight.neptune.command.roles

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object Edit : Subcommand {
    override val name = "edit"
    override val description = "Edits the title/description of a role selection."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to edit.", true),
        OptionData(OptionType.STRING, "newtitle", "The new title for the role picker.", true),
        OptionData(OptionType.STRING, "newdesc", "The new description for the role picker.", true),
        OptionData(OptionType.ATTACHMENT, "newimage", "The new image for the role picker.", false),
        OptionData(OptionType.ROLE, "newunassigned", "The new unassigned role.", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong
        val title = interaction.getOption("newtitle")!!.asString
        val desc = interaction.getOption("newdesc")!!.asString
        val unassigned = interaction.getOption("newunassigned")?.asRole
        val image = interaction.getOption("newimage")?.asAttachment

        if (image != null && !listOf("image/png", "image/jpeg").contains(image?.contentType)) {
            interaction.replyEmbeds(
                EmbedUtil.simpleEmbed("Error", "The attachment provided is not a supported image.", Config.conf.misc.error).build()
            ).setEphemeral(true).queue()

            return
        }

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", Config.conf.misc.error).build())
                .queue()

            return
        }

        if (unassigned != null) {
            selection.unassigned = unassigned.idLong
        }

        if (selection.guildId != interaction.guild!!.idLong) {

            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "The role picker of that ID does not belong to this guild.", Config.conf.misc.error).build())
                .queue()

            return
        }

        (interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).editMessageEmbedsById(
            selection.msgId,
            EmbedUtil.simpleEmbed(title, desc).setImage(image?.url).setFooter("ID: ${selection.id}").build()
        ).queue()

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Updated", "Updated that role picker.").build()).queue()

        DatabaseHandler.replaceRoleSelection(id, selection)
    }

}
