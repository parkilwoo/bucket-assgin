package bucket.batch.reader

import bucket.common.dto.LogEntry
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource

@Configuration
class UserLogReader(
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun userAndLogReader(): FlatFileItemReader<LogEntry> =
        FlatFileItemReaderBuilder<LogEntry>()
            .name("userLogReader")
            .resource(FileSystemResource("data/sample_user_log.json"))
            .lineMapper { line, _ -> objectMapper.readValue(line, LogEntry::class.java) }
            .build()
}

