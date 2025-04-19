package bucket.domain.repository

import bucket.domain.entity.UserLog
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository : JpaRepository<UserLog, Long>