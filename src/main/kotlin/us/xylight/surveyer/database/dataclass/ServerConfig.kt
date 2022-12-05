package us.xylight.surveyer.database.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val serverId: Long,
    val moderation: ModerationConfig
)

@Serializable
data class ModerationConfig(
    var warningThresh: Int = 3,
    var modlogChannel: Long = 977253966851227730
)
