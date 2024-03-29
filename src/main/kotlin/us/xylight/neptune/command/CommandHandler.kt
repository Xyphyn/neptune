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
import us.xylight.neptune.command.translate.Translate
import java.util.concurrent.TimeUnit

class CommandHandler {
    val commandClasses: List<Command> = listOf(
        Ping,
        Poll,
        Moderation,
        Warnings,
        Fun,
        Translate,
        Time,
        Roles,
        Config,
        Convert,
        Color
    )

    fun commandFromName(commandName: String): Command? {
        return commandClasses.find { it.name == commandName }
    }

    companion object {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        private val dotenv = dotenv {
            ignoreIfMissing = true
            ignoreIfMalformed = true
        }

        val deeplKey: String = dotenv["DEEPL_KEY"]
    }
}