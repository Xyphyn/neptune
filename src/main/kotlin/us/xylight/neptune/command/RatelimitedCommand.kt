package us.xylight.neptune.command

interface RatelimitedCommand : Command {
    /**
     * @property cooldown The cooldown in milliseconds before this command can be used again.
     */
    val cooldown: Long
}