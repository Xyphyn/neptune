package us.xylight.neptune.command.roles

import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object DeleteItem : Subcommand {
    override val name = "deleteitem"
    override val description = "Deletes a specified item in the role picker."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to edit.", true),
        OptionData(
            OptionType.INTEGER,
            "item",
            "What order does the role appear in? (TOP TO BOTTOM, STARTING AT 1)",
            true
        ),
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong
        val index = interaction.getOption("item")!!.asInt

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", Config.conf.misc.error).build())
                .queue()

            return
        }

        if (selection.guildId != interaction.guild!!.idLong) {

            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "The role picker of that ID does not belong to this guild.", Config.conf.misc.error).build())
                .queue()

            return
        }

        selection.roles.removeAt(index - 1)

        val selectOptions = mutableListOf<SelectOption>()

        selection.roles.forEach {
            if (it.roleId == -1L) return@forEach
            var option = SelectOption(it.label, it.roleId.toString(), it.description)
            if (it.emoji != null) option = option.withEmoji(Emoji.fromFormatted(it.emoji!!))
            selectOptions.add(option)
        }

        val selectMenu = StringSelectMenu(
            "svy:roles:menu:${selection.id}",
            "Pick your roles...",
            IntRange(0, 20),
            false,
            selectOptions
        )

        runCatching {
            (interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).editMessageById(
                selection.msgId,
                " "
            ).setActionRow(selectMenu).queue()
        }.getOrElse {
            DatabaseHandler.deleteRoleSelection(id)
        }

        DatabaseHandler.replaceRoleSelection(id, selection)

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Edited", "Edited that role in the role picker.").build()).queue()
    }
}