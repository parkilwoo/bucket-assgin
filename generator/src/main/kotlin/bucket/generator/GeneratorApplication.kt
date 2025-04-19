package bucket.generator

import bucket.domain.repository.UserLogRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["bucket.domain.repository"])
@EntityScan(basePackages = ["bucket.domain.entity"])
class GeneratorApplication

fun main() {
    val ctx = runApplication<GeneratorApplication>()
    val userLogRepository = ctx.getBean(UserLogRepository::class.java)

    val targetLogCount = 100_000
    val checkIntervalMillis = 10_000L

    println("Generator waiting for $targetLogCount logs...")

    while (true) {
        val currentCount = userLogRepository.count().toInt()
        println("current log count: $currentCount")

        if (currentCount >= targetLogCount) {
            println("Starting Generator")

            ctx.getBean(BrandGenerator::class.java).generateBrands()
            ctx.getBean(ProductGenerator::class.java).generateProducts()
            ctx.getBean(OrderGenerator::class.java).generateOrders()

            println("Generator finished.")
            break
        }
        Thread.sleep(checkIntervalMillis)
    }

    ctx.close()
}
