package us.xylight.surveyer.handler

import us.xylight.surveyer.command.Command
import us.xylight.surveyer.command.Game
import us.xylight.surveyer.command.Ping
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.command.poll.Poll

class CommandHandler {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll(),
        Game()
    )

    fun commandFromName(commandName: String): Command? {
        commandClasses.forEach { command -> if (command.name == commandName) {
            return command
        } }
        return null
    }

    companion object {
        fun subcommandFromName(subcommands: List<Subcommand>, name: String): Subcommand? {
            subcommands.forEach { subCommand -> if (subCommand.name == name) {
                return subCommand
            } }
            return null
        }
    }
}