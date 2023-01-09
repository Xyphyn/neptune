package us.xylight.neptune.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.decodeFromString
import us.xylight.neptune.database.DatabaseHandler
import us.xylight.neptune.database.dataclass.ServerConfig
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

object Config {
    // Configs are server configs. YamlConfig is basic configs, stored in resources/config.yaml.
    private val inputStream: InputStream = ClassLoader.getSystemResourceAsStream("config.yaml")
    val conf = Yaml.default.decodeFromStream<YamlConfig>(inputStream)

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
}