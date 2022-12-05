package us.xylight.surveyer.database

import com.mongodb.client.model.ReplaceOptions
import io.github.cdimascio.dotenv.Dotenv
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.database.dataclass.ModerationConfig
import us.xylight.surveyer.database.dataclass.ServerConfig
import us.xylight.surveyer.database.dataclass.Warning

object DatabaseHandler {
    private var db: CoroutineDatabase? = null
    var warnings: CoroutineCollection<Warning>? = null
    private var configs: CoroutineCollection<ServerConfig>? = null

    fun create(database: CoroutineDatabase) {
        db = database
        warnings = db!!.getCollection("warnings")
        configs = db!!.getCollection("configs")
    }

    suspend fun getConfig(serverId: Long): ServerConfig? {
        val result = configs!!.findOne(ServerConfig::serverId eq serverId)

        if (result == null)
            configs!!.insertOne(ServerConfig(serverId, ModerationConfig()))

        return configs!!.findOne(ServerConfig::serverId eq serverId)
    }

    suspend fun replaceConfig(serverId: Long, config: ServerConfig) {
        configs!!.replaceOne(ServerConfig::serverId eq serverId, config)

        Config.updateConfig(serverId, config)
    }

    suspend fun getAvailableId(): Long {
        val search = warnings!!.find(null).sort(descending(
            Warning::id
        )).limit(1)

        if (search.toList().isEmpty()) return 0

        return search.toList()[0].id + 1
    }
}