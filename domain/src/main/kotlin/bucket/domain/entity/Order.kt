package bucket.domain.entity

import bucket.domain.model.OrderStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "`order`")
class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    val deliveryAddress: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,

    val totalAmount: Int = 0,
    val discountAmount: Int = 0,
    val deliveryFee: Int = 0,

    val orderedAt: LocalDateTime = LocalDateTime.now()
)