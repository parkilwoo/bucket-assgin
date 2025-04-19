package bucket.batch.job

import bucket.batch.listener.LoggingWriteListener
import bucket.common.dto.LogEntry
import bucket.domain.entity.User
import bucket.domain.entity.UserLog
import bucket.domain.entity.UserProfile
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class UserAndLogGeneratorJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val reader: FlatFileItemReader<LogEntry>,
    private val processor: ItemProcessor<LogEntry, Triple<User, UserProfile, UserLog>>,
    private val writer: ItemWriter<Triple<User, UserProfile, UserLog>>
) {

    @Bean
    fun loadUserAndLogStep(): Step = StepBuilder("loadUserAndLogStep", jobRepository)
        .chunk<LogEntry, Triple<User, UserProfile, UserLog>>(200, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .listener(LoggingWriteListener())
        .build()

    @Bean
    fun loadUserAndLogJob(): Job = JobBuilder("loadUserAndLogJob", jobRepository)
        .start(loadUserAndLogStep())
        .incrementer(RunIdIncrementer())
        .build()
}
