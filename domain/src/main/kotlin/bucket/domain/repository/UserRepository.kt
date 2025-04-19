package bucket.domain.repository

import bucket.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByUserId(userId: String): Boolean
    fun findByUserId(userId: String): User?
    fun findAllByUserIdIn(userIds: Collection<String>): List<User>
}