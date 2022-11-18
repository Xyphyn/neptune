package us.xylight.surveyer.games

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel

enum class GameType(val create: (time: Long, channel: MessageChannel) -> Game) {
    FirstMessage({
        time, channel -> FirstMessage(time, channel)
    });

    fun createGame(time: Long, channel: MessageChannel) {
        GameManager.games.add(0, create(time, channel))
    }

    companion object {
        fun gameTypeFromString(name: String): GameType? {
            return values().find { value -> value.name == name }
        }
    }
}
