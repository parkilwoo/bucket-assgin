package bucket.batch.processor

import bucket.common.dto.LogEntry
import bucket.common.util.AesUtil
import bucket.common.util.DateTimeUtil
import bucket.domain.entity.User
import bucket.domain.entity.UserLog
import bucket.domain.entity.UserProfile
import bucket.domain.repository.UserRepository
import io.github.serpro69.kfaker.Faker
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class UserLogProcessor(
    private val userRepository: UserRepository
) {
    @Bean
    fun userAndLogProcessor(): ItemProcessor<LogEntry, Triple<User, UserProfile, UserLog>> {
        val faker = Faker()

        return ItemProcessor { log ->
            val user = userRepository.findByUserId(log.userId) ?: run {
                val name = faker.name.name()
                val email = faker.internet.email()
                val phone = faker.phoneNumber.phoneNumber()
                val createdAt = LocalDateTime.now()

                User(
                    userId = log.userId,
                    name = name,
                    email = email,
                    phoneNumber = phone,
                    password = AesUtil.encrypt("pw_${log.userId}"),
                    createdAt = createdAt
                )
            }

            val birth = faker.person.birthDate(age = faker.random.nextLong(20L..60))
            val address = faker.address.fullAddress()

            val profile = UserProfile(
                user = user,
                birthDateEncrypted = AesUtil.encrypt(birth.toString()),
                addressEncrypted = AesUtil.encrypt(address)
            )

            val logEntity = UserLog(
                user = user,
                accessTimestamp = DateTimeUtil.parse(log.accessTimeStamp),
                page = log.page
            )

            Triple(user, profile, logEntity)
        }
    }
}