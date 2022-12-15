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

object Add : Subcommand {
    override val name = "add"
    override val description = "Add a role to a selection menu."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.INTEGER, "id", "The ID of the role picker to modify.", true),
        OptionData(OptionType.ROLE, "role", "What role to add.", true),
        OptionData(OptionType.STRING, "label", "What should the role be labelled?", false).setMaxLength(20),
        OptionData(OptionType.STRING, "description", "What should the role be described as?", false).setMaxLength(100),
        OptionData(OptionType.STRING, "emoji", "What emoji should the role have?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val id = interaction.getOption("id")!!.asLong
        val role = interaction.getOption("role")!!.asRole
        val label = interaction.getOption("label")?.asString ?: role.name
        val description = interaction.getOption("description")?.asString ?: ""
        val emoji = interaction.getOption("emoji")?.asString

        val selection = DatabaseHandler.getRoleSelection(id)

        if (selection == null) {
            interaction.reply("")
                .setEmbeds(EmbedUtil.simpleEmbed("Error", "There is no role picker with that ID.", 0xff0f0f).build())
                .queue()

            return
        }

        if (selection.guildId != interaction.guild!!.idLong) return

        selection.roles.add(Role(role.idLong, label, description, emoji))

        val selectOptions = mutableListOf<SelectOption>()

        selection.roles.removeIf { it.roleId == -1L }
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

        runCatching {(interaction.guild!!.getGuildChannelById(selection.channelId) as TextChannel).editMessageById(
            selection.msgId,
            " "
        ).setActionRow(selectMenu).queue()
        }.getOrElse {
            DatabaseHandler.deleteRoleSelection(id)
        }

        DatabaseHandler.replaceRoleSelection(id, selection)

        interaction.reply("").setEphemeral(true)
            .setEmbeds(EmbedUtil.simpleEmbed("Added", "Added that role to the role picker.").build()).queue()
    }
}