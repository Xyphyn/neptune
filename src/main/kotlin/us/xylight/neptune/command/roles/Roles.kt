package us.xylight.neptune.command.roles

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.RatelimitedCommand
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.util.EmbedUtil

object Roles : RatelimitedCommand {
    override val name = "roles"
    override val description = "Commands for selection roles."
    override val options: List<OptionData> = listOf()
    override val subcommands: List<Subcommand> = listOf(Create, Add, Delete, Edit, DeleteItem)
    override val permission = Permission.MANAGE_ROLES

    override val cooldown: Long = 5_000L

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        subcommands[interaction.subcommandName]?.execute(interaction)
    }

    suspend fun onSelect(interaction: StringSelectInteractionEvent) {
        val split = interaction.componentId.split("svy:roles:menu:")
        if (split.isEmpty()) return

        val id = split[1]

        val selection = DatabaseHandler.getRoleSelection(id.toLong())


        if (selection == null) {
            interaction.reply("").setEmbeds(
                EmbedUtil.simpleEmbed(
                    "Error",
                    "Could not find that role selector in the database.",
                    0xff0f0f
                ).build()
            ).queue()

            return
        }

        val values: List<Long> = selection.roles.map { role -> role.roleId }

        if (selection.unassigned != null) {
            if (interaction.values.isEmpty()) {
                val roleId = interaction.jda.getRoleById(selection.unassigned!!)!!
                interaction.guild!!.addRoleToMember(
                    interaction.member!!,
                    roleId
                ).queue()
            } else {
                val roleId = interaction.jda.getRoleById(selection.unassigned!!)!!
                interaction.guild!!.removeRoleFromMember(
                    interaction.member!!,
                    roleId
                ).queue()
            }
        }

        values.forEach { value ->
            if (!interaction.values.contains(value.toString())) {
                val roleId = interaction.jda.getRoleById(value) ?: return@forEach
                interaction.guild!!.removeRoleFromMember(
                    interaction.member!!,
                    roleId
                ).queue()
            }
        }

        interaction.values.forEach { value ->
            val roleId = interaction.jda.getRoleById(value) ?: return@forEach

            interaction.guild!!.addRoleToMember(
                interaction.member!!,
                roleId
            ).queue()
        }

        interaction.reply("").setEmbeds(EmbedUtil.simpleEmbed("Success", "Successfully gave you those roles.").build()).setEphemeral(true).queue()
    }
}