package bucket.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "inventory")
class Inventory(
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    val product: Product,

    @Column(nullable = false)
    val quantity: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
