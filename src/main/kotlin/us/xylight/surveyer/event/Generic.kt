package us.xylight.surveyer.event

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Generic : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        // TODO Maybe add a message logger?
    }
}