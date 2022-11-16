package us.xylight.surveyer.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

class ButtonTest : ComponentCommand {
    override val name = "button"
    override val description = "test button thing"
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = emptyList()
    override val permission = null
    override val handles: MutableList<Button> = mutableListOf()

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        handles.add(0, Button.of(ButtonStyle.PRIMARY, "cool-btn", "cool"))
        handles.add(1, Button.of(ButtonStyle.PRIMARY, "2-btn", "button 2"))
        interaction.reply("").setActionRow(handles).queue()
    }

    override suspend fun onButtonClick(interaction: ButtonInteraction) {
        when (interaction.button.id) {
            "cool-btn" -> {
                interaction.reply("you clicked 'cool-btn'").queue()
            }
            "2-btn" -> {
                interaction.reply("you clicked 'button 2'").queue()
            }
        }
    }
}