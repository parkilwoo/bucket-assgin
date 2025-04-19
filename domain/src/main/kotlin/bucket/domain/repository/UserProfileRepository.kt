package bucket.domain.repository

import bucket.domain.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    @Query("SELECT up FROM UserProfile up WHERE up.user.userId IN :userIds")
    fun findAllByUserIdIn(userIds: List<String>): List<UserProfile>
}
