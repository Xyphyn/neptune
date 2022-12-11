package us.xylight.neptune.config

import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.ServerConfig

object Config {
    // TODO make config system half decentD

    private val configs = mutableMapOf<Long, ServerConfig>()

    suspend fun getConfig(serverId: Long): ServerConfig? {
        if (configs[serverId] == null) {
            val config: ServerConfig? = runCatching { DatabaseHandler.getConfig(serverId) }.getOrNull()
            if (config != null) return config
        }
        return configs[serverId]
    }

    fun updateConfig(serverId: Long, config: ServerConfig) {
        configs[serverId] = config
    }

    const val accent = 0xbd00ff
    const val wifiIcon = "<:wifi:1039979732638384179>"
    const val errorIcon = "<:BSOD:984972563358814228>"
    const val warningIcon = "<:WindowsWarning:977721596846436392>"
    const val muteIcon = "<:WindowsShieldWarning:989709614918553620>"
    const val uacIcon = "<:WindowsShieldUAC:999005696483926017>"
    const val trashIcon = "<:WindowsRecycleBin:1042228356000792586>"
    const val successIcon = "<:WindowsSuccess:977721596468928533>"
    const val logUser = "735626570399481878"
    const val banIcon = "<:WindowsShieldFailure:1042228312476483615>"
    const val loadIcon = "<a:firefoxload:1000449757543677952>"
}