package us.xylight.neptune.database.dataclass

data class RoleSelect(
    val id: Long,
    val guildId: Long,
    val roles: MutableList<Role>,
    val msgId: Long,
    val channelId: Long,
    var unassigned: Long?
)

data class Role(
    var roleId: Long,
    var label: String,
    var description: String,
    var emoji: String?
)