package us.xylight.neptune

import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import us.xylight.neptune.event.Interaction
import us.xylight.neptune.games.GameManager
import us.xylight.neptune.handler.CommandHandler
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.*
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.event.Reaction

fun main() {
    println("Starting...")

    val dotenv = dotenv {
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }
    val token = dotenv["TOKEN"]
    val mongoURI = dotenv["MONGO"]
    val database = dotenv["MONGO_DATABASE"]

    val gatewayIntents = listOf(
        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS
    )

    val client = KMongo.createClient(mongoURI).coroutine
    val db = client.getDatabase(database)

    DatabaseHandler.create(db)

    val commandHandler = CommandHandler()

    val jda = light(token, enableCoroutines = true) {
        intents += gatewayIntents
    }

    val listeners = listOf(
        Interaction(jda, commandHandler), GameManager(jda), Reaction(jda, commandHandler)
    )


    val commands = jda.updateCommands()

    for (command in commandHandler.commandClasses) {
        val data = Commands.slash(command.name, command.description)
            .addOptions(command.options).setGuildOnly(true)

        command.subcommands.forEach {
            subcommand -> data.addSubcommands(
                SubcommandData(subcommand.name, subcommand.description).addOptions(subcommand.options)
            )
        }

        commands.addCommands(data)
    }

    commands.queue()


    jda.awaitReady()
    println("Ready.")
}
