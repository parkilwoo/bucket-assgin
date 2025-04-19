package bucket.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", unique = true)
    val userId: String = "",

    val password: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
)