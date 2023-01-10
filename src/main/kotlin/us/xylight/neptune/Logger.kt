package us.xylight.neptune

import us.xylight.neptune.util.Color

enum class LogLevel(val idColor: String, val textColor: String) {
    ERROR(Color.RED_BOLD_BRIGHT, Color.RED_BRIGHT),
    WARNING(Color.YELLOW_BOLD_BRIGHT, Color.YELLOW_BRIGHT),
    INFO(Color.BLUE_BOLD, Color.BLUE),
    VERBOSE(Color.BLUE_BOLD_BRIGHT, Color.BLUE_BRIGHT),
    DEBUG(Color.GREEN_BOLD_BRIGHT, Color.GREEN_BRIGHT)
}

object Logger {

    var logLevel = LogLevel.VERBOSE

    fun log(message: String, level: LogLevel) {
        if (level.ordinal <= logLevel.ordinal) {
            println("[${level.idColor}${level.name}${Color.RESET}] ${level.textColor}$message${Color.RESET}")
        }
    }
}