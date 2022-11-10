package us.xylight.surveyer.handler

import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Ping
import us.xylight.surveyer.command.Poll

class CommandHandler {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll()
    )

    fun commandFromName(commandName: String): Command? {
        commandClasses.forEach { command -> if (command.name == commandName) {
            return command
        } }
        return null
    }
}