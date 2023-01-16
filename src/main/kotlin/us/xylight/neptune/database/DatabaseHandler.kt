package us.xylight.neptune.database

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.dataclass.*

object DatabaseHandler {
    private lateinit var db: CoroutineDatabase
    lateinit var warnings: CoroutineCollection<Warning>
    private lateinit var configs: CoroutineCollection<ServerConfig>
    lateinit var roles: CoroutineCollection<RoleSelect>

    fun create(database: CoroutineDatabase) {
        db = database
        warnings = db.getCollection(Config.conf.database.warnings)
        configs = db.getCollection(Config.conf.database.configs)
        roles = db.getCollection(Config.conf.database.roles)
    }

    suspend fun getRoleSelection(selectId: Long): RoleSelect? {
        return roles.findOne(RoleSelect::id eq selectId)
    }

    suspend fun replaceRoleSelection(selectId: Long, roleSelect: RoleSelect) {
        roles.replaceOne(RoleSelect::id eq selectId, roleSelect)
    }

    suspend fun addRoleSelection(roleSelect: RoleSelect) {
        roles.insertOne(roleSelect)
    }

    suspend fun deleteRoleSelection(selectId: Long) {
        roles.deleteOne(RoleSelect::id eq selectId)
    }

    suspend fun getAvailableRoleSelectId(): Long {
        val search = roles.find(null).sort(descending(
            RoleSelect::id
        )).limit(1)

        if (search.toList().isEmpty()) return 0

        return search.toList()[0].id + 1
    }
    suspend fun getConfig(serverId: Long): ServerConfig? {
        val result = configs.findOne(ServerConfig::serverId eq serverId)

        if (result == null)
            configs.insertOne(ServerConfig(serverId, ModerationConfig(), TranslationConfig()))

        return configs.findOne(ServerConfig::serverId eq serverId)
    }

    suspend fun replaceConfig(serverId: Long, config: ServerConfig) {
        configs.replaceOne(ServerConfig::serverId eq serverId, config)

        Config.updateConfig(serverId, config)
    }

    suspend fun getAvailableWarningId(): Long {
        val search = warnings.find(null).sort(descending(
            Warning::id
        )).limit(1)

        if (search.toList().isEmpty()) return 0

        return search.toList()[0].id + 1
    }
}