package bucket.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["bucket.domain.repository"])
@EntityScan(basePackages = ["bucket.domain.entity"])
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}