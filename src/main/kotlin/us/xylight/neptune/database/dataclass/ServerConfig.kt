package us.xylight.neptune.database.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val serverId: Long,
    val moderation: ModerationConfig,
    val translation: TranslationConfig
)

@Serializable
data class ModerationConfig(
    var warningThresh: Int = 3,
    var modlogChannel: Long = 977253966851227730
)

@Serializable
data class TranslationConfig(
    var reactions: Boolean = true,
    var enabled: Boolean = true
)