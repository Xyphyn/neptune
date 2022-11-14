package us.xylight.surveyer.games

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener

class GameManager : EventListener {
    companion object {
        var games: MutableList<Game> = mutableListOf(

        )

        fun onEnd(game: Game) {
            games = games.filterNot { g -> g == game
            } as MutableList<Game>
        }
    }

    override fun onEvent(event: GenericEvent) {
        games.forEach {
            game -> game.onEvent(event)
        }
    }

}