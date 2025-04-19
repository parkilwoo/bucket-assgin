package bucket.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "brand")
class Brand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String = "",
    val deliveryFee: Int = 0
)