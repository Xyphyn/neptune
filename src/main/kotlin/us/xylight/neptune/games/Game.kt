package us.xylight.neptune.games

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.GenericEvent
import us.xylight.neptune.util.EmbedUtil
import java.util.*
import kotlin.concurrent.schedule

abstract class Game(time: Long, channel: MessageChannel) {
    abstract val name: String
    abstract val description: String

    abstract fun onEvent(event: GenericEvent)
    abstract fun onEnd()
}