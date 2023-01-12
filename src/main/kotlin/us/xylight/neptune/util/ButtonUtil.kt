package us.xylight.neptune.util

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button

object ButtonUtil {
    fun disableButtons(buttons: List<Button>): ActionRow {
        if (buttons.isEmpty()) {
            throw IllegalArgumentException("Param buttons requires at least one button.")
        }

        return ActionRow.of(buttons.map {
            it.asDisabled()
        })
    }
}