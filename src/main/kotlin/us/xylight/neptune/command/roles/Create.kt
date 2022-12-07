package us.xylight.neptune.command.roles

import dev.minn.jda.ktx.interactions.components.EntitySelectMenu
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.Role
import us.xylight.neptune.database.dataclass.RoleSelect
import us.xylight.neptune.util.EmbedUtil

class Create : Subcommand {
    override val name = "create"
    override val description = "Creates a role selection menu."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "title", "What roles is this selector for?", false),
        OptionData(OptionType.STRING, "description", "What should the description be?", false),
        OptionData(OptionType.ROLE, "unassigned", "What role should be used if the user has none?", false)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val title = interaction.getOption("title")?.asString ?: "Roles"
        val description = interaction.getOption("description")?.asString ?: "Select your roles here!"
        val unassigned = interaction.getOption("unassigned")?.asRole
        interaction.deferReply().queue()

        val availableRoleId = DatabaseHandler.getAvailableRoleSelectId()

        val selectMenu = StringSelectMenu(
            "svy:roles:menu:$availableRoleId",
            "Pick your roles...",
            IntRange(1, 20),
            false,
            listOf(
                SelectOption("?", "null", "Use /roles add to add some roles!")
            )
        )

        interaction.hook.editOriginalEmbeds(EmbedUtil.simpleEmbed(title, description).setFooter("ID: $availableRoleId").build())
            .setActionRow(selectMenu).queue()

        val message = interaction.hook.retrieveOriginal().complete()

        DatabaseHandler.addRoleSelection(
            RoleSelect(
                availableRoleId,
                interaction.guild!!.idLong,
                mutableListOf(
                    Role(-1, "?", "Use /roles add to add some roles!", null)
                ),
                message.idLong,
                message.channel.idLong,
                unassigned?.idLong
            )
        )
    }
}
