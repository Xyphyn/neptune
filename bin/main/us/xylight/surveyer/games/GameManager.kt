package us.xylight.surveyer.games

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener

class GameManager(jda: JDA) {
    companion object {
        var games: MutableList<Game> = mutableListOf(

        )

        fun onEnd(game: Game) {
            games = games.filterNot { g -> g == game } as MutableList<Game>
        }
    }

    init {
        jda.listener<GenericEvent> {
            games.forEach {
                game -> game.onEvent(it)
            }
        }
    }

}