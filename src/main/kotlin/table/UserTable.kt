package table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Users : Table("user") {
    val id = long("id").autoIncrement()
    val userId = varchar("user_id", 50).uniqueIndex()
    val password = varchar("password", 255) // 암호화된 비밀번호
    val name = varchar("name", 100)
    val email = varchar("email", 100)
    val phoneNumber = varchar("phone_number", 20)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}