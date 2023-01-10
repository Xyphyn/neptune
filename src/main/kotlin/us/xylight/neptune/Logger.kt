package us.xylight.neptune

import us.xylight.neptune.util.Color

enum class LogLevel(val priority: Int, val color: String) {
    ERROR(1, Color.RED_BRIGHT),
    WARNING(2, Color.YELLOW),
    INFO(3, Color.BLUE),
    VERBOSE(4, Color.BLUE_BRIGHT),
    DEBUG(5, Color.GREEN)
}

object Logger {

    var logLevel = LogLevel.VERBOSE

    fun log(message: String, level: LogLevel) {
        if (level.priority <= logLevel.priority) {
            println("${level.color} [${level.name}] $message ${Color.RESET}")
        }
    }
}