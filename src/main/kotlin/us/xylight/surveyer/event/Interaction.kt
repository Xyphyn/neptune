package us.xylight.surveyer.event

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import us.xylight.surveyer.handler.CommandHandler

class Interaction(private val commandHandler: CommandHandler) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val command = commandHandler.commandFromName(event.name) ?: return
        command.execute(event)
    }
}