package us.xylight.neptune.database

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.dataclass.ModerationConfig
import us.xylight.neptune.database.dataclass.RoleSelect
import us.xylight.neptune.database.dataclass.ServerConfig
import us.xylight.neptune.database.dataclass.Warning

object DatabaseHandler {
    private var db: CoroutineDatabase? = null
    var warnings: CoroutineCollection<Warning>? = null
    private var configs: CoroutineCollection<ServerConfig>? = null
    private var roles: CoroutineCollection<RoleSelect>? = null

    fun create(database: CoroutineDatabase) {
        db = database
        warnings = db!!.getCollection("warnings")
        configs = db!!.getCollection("configs")
        roles = db!!.getCollection("roles")
    }

    suspend fun getRoleSelection(selectId: Long): RoleSelect? {
        return roles!!.findOne(RoleSelect::id eq selectId)
    }

    suspend fun replaceRoleSelection(selectId: Long, roleSelect: RoleSelect) {
        roles!!.replaceOne(RoleSelect::id eq selectId, roleSelect)
    }

    suspend fun addRoleSelection(roleSelect: RoleSelect) {
        roles!!.insertOne(roleSelect)
    }

    suspend fun deleteRoleSelection(selectId: Long) {
        roles!!.deleteOne(RoleSelect::id eq selectId)
    }

    suspend fun getAvailableRoleSelectId(): Long {
        val search = roles!!.find(null).sort(descending(
            RoleSelect::id
        )).limit(1)

        if (search.toList().isEmpty()) return 0

        return search.toList()[0].id + 1
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



    suspend fun getAvailableWarningId(): Long {
        val search = warnings!!.find(null).sort(descending(
            Warning::id
        )).limit(1)

        if (search.toList().isEmpty()) return 0

        return search.toList()[0].id + 1
    }
}