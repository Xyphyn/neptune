package us.xylight.neptune.util

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class DateParser {
    companion object {

        private val pattern = Pattern.compile("(\\d+)([hmsdw])")
        // <number>[s, m, h, d]
        fun millisFromTime(time: String): Long {
            val matcher = pattern.matcher(time)
            var totalMillis: Long = 0
            while (matcher.find()) {
                val duration: Int = matcher.group(1).toInt()
                val interval: TimeUnit = toTimeUnit(matcher.group(2))
                val l: Long = interval.toMillis(duration.toLong())
                totalMillis += l
            }

            return totalMillis
        }

        private fun toTimeUnit(c: String?): TimeUnit {
            return when (c) {
                "s" -> TimeUnit.SECONDS
                "m" -> TimeUnit.MINUTES
                "h" -> TimeUnit.HOURS
                "d" -> TimeUnit.DAYS
                else -> throw IllegalArgumentException(String.format("%s is not a valid code [smhd]", c))
            }
        }
    }
}