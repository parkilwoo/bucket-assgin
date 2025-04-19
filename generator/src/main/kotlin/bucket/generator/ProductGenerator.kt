package bucket.generator

import bucket.domain.entity.Inventory
import bucket.domain.entity.Product
import bucket.domain.repository.BrandRepository
import bucket.domain.repository.InventoryRepository
import bucket.domain.repository.ProductRepository
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.commerce.Faker as CFaker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductGenerator(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository
) {
    private val faker = Faker()
    private val cFaker = CFaker()
    private val sizes = listOf("S", "M", "L", "XL")

    @Transactional
    fun generateProducts(totalCount: Int = 100) {
        val brands = brandRepository.findAll().shuffled()
        require(brands.isNotEmpty()) { "브랜드가 먼저 생성되어야 합니다." }

        val brandProductMap = mutableListOf<Pair<Long, Int>>()

        // 1. 각 브랜드에 최소 1개 보장
        brands.forEach { brand ->
            brandProductMap.add(brand.id to 1)
        }

        // 2. 나머지 개수 랜덤 분배
        val remaining = totalCount - brands.size
        repeat(remaining) {
            val randBrand = brands.random()
            val idx = brandProductMap.indexOfFirst { it.first == randBrand.id }
            brandProductMap[idx] = randBrand.id to (brandProductMap[idx].second + 1)
        }

        var productIdx = 1

        brandProductMap.forEach { (brandId, count) ->
            val brand = brands.first { it.id == brandId }

            repeat(count) {
                val name = cFaker.commerce.productName()
                val color = faker.color.name()
                val size = sizes.random()
                val productCode = "$name-$color".uppercase()
                val price = (10000..100000 step 500).toList().random()
                val quantity = (10..100).random()

                val product = productRepository.save(
                    Product(
                        productCode = productCode,
                        name = name,
                        color = color,
                        size = size,
                        price = price,
                        brand = brand
                    )
                )

                inventoryRepository.save(
                    Inventory(
                        product = product,
                        quantity = quantity
                    )
                )

                productIdx++
            }
        }

        println("총 ${totalCount}개 상품 생성 완료")
    }
}
