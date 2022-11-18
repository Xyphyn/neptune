package us.xylight.surveyer.games

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import us.xylight.surveyer.util.EmbedUtil
import java.util.Timer
import kotlin.concurrent.schedule

class FirstMessage(time: Long, private val channel: MessageChannel) : Game(time, channel) {
    override val name = "First Message"
    override val description = "First person to reply to this message wins!"

    init {
        val embed = EmbedUtil.simpleEmbed(name, description)
        channel.sendMessage("").setEmbeds(embed.build()).queue()
        Timer().schedule(time) { onEnd() }
    }

    override fun onEvent(event: GenericEvent) {
        if (event !is MessageReceivedEvent) return
        if (event.channel != channel) return
        if (event.message.author == event.jda.selfUser) return
        event.message.reply("congrats you win wooooo").queue()
        onEnd()
    }

    override fun onEnd() {
        GameManager.onEnd(this)
    }
}