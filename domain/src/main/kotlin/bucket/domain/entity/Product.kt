package bucket.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val productCode: String = "",
    val name: String = "",
    val color: String = "",
    val size: String = "",
    val price: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    val brand: Brand
)