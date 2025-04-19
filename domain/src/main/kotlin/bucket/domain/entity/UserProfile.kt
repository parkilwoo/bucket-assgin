package bucket.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_profile")
class UserProfile(
    @Id
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "birth_date_encrypted", nullable = false)
    val birthDateEncrypted: String,

    @Column(name = "address_encrypted", nullable = false)
    val addressEncrypted: String
)
