package table

import org.jetbrains.exposed.sql.Table

object OrderItems : Table("order_item") {
    val id = long("id").autoIncrement()
    val orderId = long("order_id").references(Orders.id)
    val productId = long("product_id").references(Products.id)
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)
}
