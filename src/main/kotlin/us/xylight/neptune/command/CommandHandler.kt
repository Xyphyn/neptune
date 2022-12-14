package us.xylight.neptune.command

import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import us.xylight.neptune.command.config.Config
import us.xylight.neptune.command.convert.Convert
import us.xylight.neptune.command.`fun`.Fun
import us.xylight.neptune.command.moderation.Moderation
import us.xylight.neptune.command.poll.Poll
import us.xylight.neptune.command.roles.Roles
import us.xylight.neptune.command.time.Time
import java.util.concurrent.TimeUnit

class CommandHandler {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll(),
        Moderation(this),
        Warnings(),
        Fun(),
        Translate(),
        Time(),
        Roles(),
        Config(),
        Convert()
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