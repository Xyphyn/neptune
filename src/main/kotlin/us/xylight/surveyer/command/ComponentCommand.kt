package us.xylight.surveyer.command

import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction

interface ComponentCommand : Command {
    val handles: List<Button>

    suspend fun onButtonClick(interaction: ButtonInteraction)
}