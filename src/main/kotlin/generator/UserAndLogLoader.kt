package generator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.serpro69.kfaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import table.*
import util.AesUtil
import java.io.File
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime as JLocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.datetime.toKotlinLocalDateTime


data class LogEntry(
    val access_timestamp: String,
    val user_id: String,
    val page: String
)

object UserAndLogLoader {
    private val objectMapper = jacksonObjectMapper()
    private val faker = Faker()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun loadUsersAndLogs(logPath: String) {
        val file = File(logPath)
        val logs: List<LogEntry> = File(logPath)
            .readLines()
            .mapNotNull { line ->
                try {
                    objectMapper.readValue(line, LogEntry::class.java)
                } catch (e: Exception) {
                    println("JSON 파싱 에러: $line")
                    null
                }
            }

        val userIdSet = logs.map { it.user_id }.toSet()
        val userIdToDbId = mutableMapOf<String, Long>()

        transaction {
            println("총 ${userIdSet.size}명의 유저 ID로 생성 시작")

            // 1. Users + Profiles 생성
            userIdSet.forEachIndexed { idx, rawUserId ->
                val name = faker.name.name()
                val email = faker.internet.email()
                val phone = faker.phoneNumber.phoneNumber()
                val address = faker.address.fullAddress()
                val birth = faker.person.birthDate(age = faker.random.nextLong(longRange = 1L .. 100L))
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                val userId = Users.insert {
                    it[userId] = rawUserId
                    it[password] = AesUtil.encrypt("pw_$idx")
                    it[Users.name] = name
                    it[Users.email] = email
                    it[phoneNumber] = phone
                    it[createdAt] = now
                } get Users.id

                UserProfiles.insert {
                    it[UserProfiles.userId] = userId
                    it[birthDateEncrypted] = AesUtil.encrypt(birth.toString())
                    it[addressEncrypted] = AesUtil.encrypt(address)
                }

                userIdToDbId[rawUserId] = userId
            }

            println("유저 데이터 생성 완료")

            // 2. 로그 전체 적재
            logs.forEach {
                val userDbId = userIdToDbId[it.user_id] ?: return@forEach
                val accessTime = JLocalDateTime.parse(it.access_timestamp, formatter).toKotlinLocalDateTime()
                val dbPage = it.page

                UserLogs.insert {
                    it[accessTimestamp] = accessTime
                    it[userId] = userDbId
                    it[page] = dbPage
                }
            }

            println("총 ${logs.size}건 로그 데이터 생성 완료")
        }
    }
}
