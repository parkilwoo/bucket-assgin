package table

import org.jetbrains.exposed.sql.Table

object UserProfiles : Table("user_profile") {
    val userId = long("user_id").references(Users.id).uniqueIndex()
    val birthDateEncrypted = varchar("birth_date_encrypted", 255)
    val addressEncrypted = varchar("address_encrypted", 255)

    override val primaryKey = PrimaryKey(userId)
}
