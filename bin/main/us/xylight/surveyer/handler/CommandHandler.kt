package us.xylight.surveyer.handler

import us.xylight.surveyer.command.*
import us.xylight.surveyer.command.`fun`.Fun
import us.xylight.surveyer.command.`fun`.Reddit
import us.xylight.surveyer.command.moderation.Moderation
import us.xylight.surveyer.command.poll.Poll
import us.xylight.surveyer.database.DatabaseHandler

class CommandHandler(db: DatabaseHandler) {
    val commandClasses: List<Command> = listOf(
        Ping(),
        Poll(),
        Game(),
        Moderation(db, this),
        Warnings(db),
        Fun()
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
    }
}