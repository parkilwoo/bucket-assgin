package table

import org.jetbrains.exposed.sql.Table

object Brands : Table("brand") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val deliveryFee = integer("delivery_fee")

    override val primaryKey = PrimaryKey(id)
}
