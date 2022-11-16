package us.xylight.surveyer.database.dataclass

data class Warning (
    val guild: String,
    val user: String,
    val reason: String,
    val moderator: String,
    val time: Long,
    val id: Long
)
