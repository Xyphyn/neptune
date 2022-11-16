package us.xylight.surveyer.handler

import us.xylight.surveyer.command.*
import us.xylight.surveyer.command.moderation.Moderation
import us.xylight.surveyer.command.poll.Poll
import us.xylight.surveyer.database.DatabaseHandler

class CommandHandler(db: DatabaseHandler) {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll(),
        Game(),
        Moderation(db),
        Warnings(db)
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