package us.xylight.neptune

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.event.Interaction
import us.xylight.neptune.event.Reaction

val dotenv = dotenv {
    ignoreIfMissing = true
    ignoreIfMalformed = true
}

fun main() {
    val logLevel = dotenv["LOGLEVEL"]
    LogLevel.values().find { it.name.lowercase() == logLevel.lowercase() }?.let { Logger.logLevel = it }


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


    Interaction(jda, commandHandler)
    Reaction(jda)

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
    jda.guilds.forEach { guild ->
        println("${guild.name}: ${guild.memberCount} | ${guild.id}")
    }
    println("Ready.")

    jda.listener<GuildJoinEvent> {
        println("New guild joined: ${it.guild.name} - ${it.guild.memberCount} members")
        jda.presence.setPresence(Activity.watching("${jda.guilds.size} guilds"), false)
    }

    jda.presence.setPresence(Activity.watching("${jda.guilds.size} guilds"), false)
}
