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
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Role
import us.xylight.neptune.util.EmbedUtil

class EditItem : Subcommand {
    override val name = "edititem"
    override val description = "Edits a specified item in the role picker."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to edit.", true),
        OptionData(
            OptionType.INTEGER,
            "item",
            "What order does the role appear in? (TOP TO BOTTOM, STARTING AT 1)",
            true
        ),
        OptionData(OptionType.ROLE, "role", "What role to change it to", true),
        OptionData(OptionType.STRING, "label", "What should the new label be?", false).setMaxLength(20),
        OptionData(OptionType.STRING, "description", "What should the role be described as?", false).setMaxLength(100),
        OptionData(OptionType.STRING, "emoji", "What emoji should the role have?", false),
        OptionData(OptionType.BOOLEAN, "delete", "Should it be deleted?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong
        val index = interaction.getOption("item")!!.asInt
        val role = interaction.getOption("role")!!.asRole
        val label = interaction.getOption("label")?.asString
        val description = interaction.getOption("description")?.asString
        val emoji = interaction.getOption("emoji")?.asString
        val delete = interaction.getOption("delete")?.asBoolean ?: false

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", 0xff0f0f).build())
                .queue()

            return
        }

        if (selection.guildId != interaction.guild!!.idLong) return

        selection.roles.removeAt(index)
        selection.roles.add(
            index,
            Role(
                role.idLong,
                label ?: selection.roles[index].label,
                description ?: selection.roles[index].description,
                emoji ?: selection.roles[index].emoji
            )
        )

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
            IntRange(1, 20),
            false,
            selectOptions
        )

        runCatching {
            (interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).editMessageById(
                selection.msgId,
                " "
            ).setActionRow(selectMenu).queue()
        }.getOrElse { error ->
            DatabaseHandler.deleteRoleSelection(id)
        }

        DatabaseHandler.replaceRoleSelection(id, selection)

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Edited", "Edited that role in the role picker.").build()).queue()
    }
}