package bucket.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 로그 파일에서 JSON을 읽어 바인딩할 Data
 */
data class LogEntry(
    @JsonProperty("access_timestamp")
    val accessTimeStamp: String,
    @JsonProperty("user_id")
    val userId: String,
    val page: String
)