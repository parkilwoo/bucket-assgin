package bucket.generator

import bucket.domain.entity.Brand
import bucket.domain.repository.BrandRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrandGenerator(
    private val brandRepository: BrandRepository
) {
    @Transactional
    fun generateBrands() {
        val brandNames = ('A'..'Z').map { "Brand $it" } // Brand A ~ Brand Z
        val deliveryFees = (1000..10000 step 300).shuffled().take(26)

        val brands = brandNames.mapIndexed { i, brandName ->
            Brand(
                name = brandName,
                deliveryFee = deliveryFees[i]
            )
        }

        brandRepository.saveAll(brands)
        println("총 ${brandNames.size}개 브랜드 생성 완료")
    }
}
