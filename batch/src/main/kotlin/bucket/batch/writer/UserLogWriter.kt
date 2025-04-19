package bucket.batch.writer

import bucket.domain.entity.User
import bucket.domain.entity.UserLog
import bucket.domain.entity.UserProfile
import bucket.domain.repository.UserLogRepository
import bucket.domain.repository.UserProfileRepository
import bucket.domain.repository.UserRepository
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class UserLogWriter(
    private val userRepository: UserRepository,
    private val userLogRepository: UserLogRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    @Bean
    fun userAndLogWriter(): ItemWriter<Triple<User, UserProfile, UserLog>> {
        return ItemWriter { chunk ->
            val userMap = mutableMapOf<String, User>()
            val profileMap = mutableMapOf<String, Pair<String, String>>() // userId -> (birthEncrypted, addressEncrypted)
            val logList = mutableListOf<Triple<String, LocalDateTime, String>>() // userId, timestamp, page

            // 중복 제거 및 데이터 정리
            chunk.forEach { (user, profile, log) ->
                if (!userMap.containsKey(user.userId)) {
                    userMap[user.userId] = user
                    profileMap[user.userId] = profile.birthDateEncrypted to profile.addressEncrypted
                }
                logList.add(Triple(user.userId, log.accessTimestamp, log.page))
            }

            // DB에 이미 존재하는 유저 조회
            val allUserIds = userMap.keys.toList()
            val existingUsers = userRepository.findAllByUserIdIn(allUserIds)
            val existingUserIds = existingUsers.map { it.userId }.toSet()

            val newUsers = userMap.values.filter { it.userId !in existingUserIds }
            userRepository.saveAll(newUsers)

            // 최종 저장된 유저 맵 구성
            val finalUsers = userRepository.findAllByUserIdIn(allUserIds)
                .associateBy { it.userId }

            // UserProfile 생성
            val newProfiles = profileMap.mapNotNull { (userId, pair) ->
                val user = finalUsers[userId] ?: return@mapNotNull null
                UserProfile(
                    user = user,
                    birthDateEncrypted = pair.first,
                    addressEncrypted = pair.second
                )
            }

            // 중복 방지: userId 기준으로 이미 DB에 있는 user_profile은 생성하지 않음
            val existingProfileUserIds = userProfileRepository.findAllByUserIdIn(allUserIds)
                .map { it.user.userId }
                .toSet()

            val filteredProfiles = newProfiles.filter { it.user.userId !in existingProfileUserIds }
            userProfileRepository.saveAll(filteredProfiles)

            // UserLog 생성
            val logs = logList.mapNotNull { (userId, timestamp, page) ->
                val user = finalUsers[userId] ?: return@mapNotNull null
                UserLog(user = user, accessTimestamp = timestamp, page = page)
            }
            userLogRepository.saveAll(logs)

            println("저장 완료: Users=${newUsers.size}, Profiles=${filteredProfiles.size}, Logs=${logs.size}")
        }
    }
}
