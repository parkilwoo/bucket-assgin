package generator

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import table.Brands

object BrandGenerator {
    fun generateBrands() {
        val brandNames = ('A'..'Z').map { "Brand $it" }
        val deliveryFees = (1000..10000 step 300).shuffled().take(26)

        transaction {
            brandNames.forEachIndexed { i, brandName ->
                Brands.insert {
                    it[name] = brandName
                    it[deliveryFee] = deliveryFees[i]
                }
            }
            println("총 ${brandNames.size}개 브랜드 생성 완료")
        }
    }
}
