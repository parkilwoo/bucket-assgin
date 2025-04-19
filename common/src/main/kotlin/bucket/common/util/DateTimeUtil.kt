package bucket.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtil {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun parse(timeStamp: String): LocalDateTime = LocalDateTime.parse(timeStamp, formatter)
}