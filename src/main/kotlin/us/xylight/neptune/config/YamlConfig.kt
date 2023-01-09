package us.xylight.neptune.config

import kotlinx.serialization.Serializable

@Serializable
data class YamlConfig(
    val emoji: Emojis,
    val user: UserConfig,
    val misc: MiscConfig,
    val database: DatabaseConfig
)

@Serializable
data class Emojis(
    val wifi: String,
    val error: String,
    val warning: String,
    val mute: String,
    val uac: String,
    val trash: String,
    val success: String,
    val ban: String,
    val load: String
)

@Serializable
data class UserConfig(
    val logging: Long
)

@Serializable
data class MiscConfig(
    val accent: Int
)

@Serializable
data class DatabaseConfig(
    val warnings: String,
    val configs: String,
    val roles: String
)