package us.xylight.surveyer.database

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.descending
import us.xylight.surveyer.database.dataclass.Warning

class DatabaseHandler(db: CoroutineDatabase) {
    val warnings: CoroutineCollection<Warning> = db.getCollection("warnings")

    suspend fun getAvailableId(): Long {
        val search = warnings.find(null).sort(descending(
            Warning::id
        )).limit(1)

        return search.toList()[0].id + 1
    }
}