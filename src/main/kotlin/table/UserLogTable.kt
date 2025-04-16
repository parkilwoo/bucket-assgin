package table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserLogs : Table("user_log") {
    val id = long("id").autoIncrement()
    val accessTimestamp = datetime("access_timestamp")
    val userId = long("user_id").references(Users.id)
    val page = varchar("page", 100)

    override val primaryKey = PrimaryKey(id)
}
