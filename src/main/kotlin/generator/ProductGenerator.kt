package generator

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.commerce.Faker as CFaker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import table.*

object ProductGenerator {
    private val faker = Faker()
    private val cFaker = CFaker()
    private val sizes = listOf("S", "M", "L", "XL")

    fun generateProducts(totalCount: Int = 100) {
        transaction {
            val brandIds = Brands.selectAll().map { it[Brands.id] }.shuffled()

            // 1. 모든 브랜드에 최소 1개 할당
            val brandProductMap = mutableListOf<Pair<Long, Int>>() // (brandId, n)

            brandIds.forEach { brandId ->
                brandProductMap.add(brandId to 1)
            }

            // 2. 나머지 개수는 랜덤 분배
            val remaining = totalCount - brandIds.size
            repeat(remaining) {
                val randBrand = brandIds.random()
                val idx = brandProductMap.indexOfFirst { it.first == randBrand }
                brandProductMap[idx] = randBrand to (brandProductMap[idx].second + 1)
            }

            var productIdx = 1

            // 3. 상품 및 재고 생성
            brandProductMap.forEach { (brandId, count) ->
                repeat(count) {
                    val name = cFaker.commerce.productName()
                    val color = faker.color.name()
                    val size = sizes.random()
                    val code = "$name-$color".uppercase()
                    val price = (10000..100000 step 500).toList().random()
                    val quantity = (10..100).random()

                    val productId = Products.insert {
                        it[productCode] = code
                        it[Products.name] = name
                        it[Products.color] = color
                        it[Products.size] = size
                        it[Products.price] = price
                        it[Products.brandId] = brandId
                    } get Products.id

                    Inventories.insert {
                        it[Inventories.productId] = productId
                        it[Inventories.quantity] = quantity
                    }

                    productIdx++
                }
            }

            println("총 ${totalCount}개 상품 생성 완료")
        }
    }
}
