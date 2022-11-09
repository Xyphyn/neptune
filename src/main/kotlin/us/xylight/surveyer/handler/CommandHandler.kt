package us.xylight.surveyer.handler

import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Ping

class CommandHandler {
    val commandClasses: List<Command> = listOf(
        Ping()
    )

    fun commandFromName(commandName: String): Command? {
        commandClasses.forEach { command -> if (command.name == commandName) {
            return command
        } }
        return null
    }
}