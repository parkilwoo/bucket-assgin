package table

import org.jetbrains.exposed.sql.Table

object Products : Table("product") {
    val id = long("id").autoIncrement()
    val productCode = varchar("product_code", 100).uniqueIndex() // eg: SHIRT-BLACK
    val name = varchar("name", 255)
    val color = varchar("color", 50)
    val size = varchar("size", 10)
    val price = integer("price")
    val brandId = long("brand_id").references(Brands.id)

    override val primaryKey = PrimaryKey(id)
}
