package us.xylight.surveyer

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import us.xylight.surveyer.event.Generic
import us.xylight.surveyer.event.Interaction
import us.xylight.surveyer.games.GameManager
import us.xylight.surveyer.handler.CommandHandler

fun main() {
    println("Starting...")

    val dotenv = dotenv()
    val token = dotenv["TOKEN"]

    val intents = listOf(
        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES
    )

    val commandHandler = CommandHandler()
    val gameManager = GameManager()

    val jda = JDABuilder.createLight(token)
        .enableIntents(intents)
        .addEventListeners(Generic(), Interaction(commandHandler), gameManager)
        .build()

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
