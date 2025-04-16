package table

import org.jetbrains.exposed.sql.Table

object Inventories : Table("inventory") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(Products.id).uniqueIndex()
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)
}
