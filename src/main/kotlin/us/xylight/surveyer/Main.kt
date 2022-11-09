package us.xylight.surveyer

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import us.xylight.surveyer.event.Generic
import us.xylight.surveyer.event.Slash
import us.xylight.surveyer.handler.CommandHandler

fun main() {
    println("Starting...")

    val dotenv = dotenv()
    val token = dotenv["TOKEN"]

    val intents = listOf(
        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES
    )

    val commandHandler = CommandHandler()

    val jda = JDABuilder.createLight(token)
        .enableIntents(intents)
        .addEventListeners(Generic(), Slash(commandHandler))
        .build()

    val commands = jda.updateCommands()

    commandHandler.commandClasses.forEach { command ->
        commands.addCommands(
            Commands.slash(command.name, command.description)
            .addOptions(command.options).setGuildOnly(true))
    }

    commands.queue()

    jda.awaitReady()
    println("Ready.")
}
