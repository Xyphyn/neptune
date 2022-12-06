package us.xylight.surveyer.handler

import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import us.xylight.surveyer.command.*
import us.xylight.surveyer.command.`fun`.Fun
import us.xylight.surveyer.command.moderation.Moderation
import us.xylight.surveyer.command.poll.Poll
import us.xylight.surveyer.command.time.Time
import us.xylight.surveyer.database.DatabaseHandler
import java.util.concurrent.TimeUnit

class CommandHandler() {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll(),
        Game(),
        Moderation(this),
        Warnings(),
        Fun(),
        Translate(),
        Time()
    )

    fun commandFromName(commandName: String): Command? {
        return commandClasses.find { it.name == commandName }
    }

    companion object {
        fun subcommandFromName(subcommands: List<Subcommand>, name: String): Subcommand? {
            subcommands.forEach { subCommand -> if (subCommand.name == name) {
                return subCommand
            } }
            return null
        }

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
        private val dotenv = dotenv {
            ignoreIfMissing = true
            ignoreIfMalformed = true
        }
        val translateServer: String = dotenv["TRANSLATOR_URL"]
    }
}