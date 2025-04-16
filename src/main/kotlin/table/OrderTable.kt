package table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Orders : Table("order") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id)
    val deliveryAddress = varchar("delivery_address", 255)
    val status = varchar("status", 20)
    val totalAmount = integer("total_amount")
    val discountAmount = integer("discount_amount")
    val deliveryFee = integer("delivery_fee")
    val orderedAt = datetime("ordered_at")

    override val primaryKey = PrimaryKey(id)
}
