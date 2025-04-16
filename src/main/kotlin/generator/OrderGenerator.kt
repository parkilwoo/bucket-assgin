package generator

import io.github.serpro69.kfaker.Faker
import model.OrderStatus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import table.*
import java.time.LocalDate
import java.time.LocalDateTime as JLocalDateTime
import java.time.LocalTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.temporal.ChronoUnit

object OrderGenerator {
    private val faker = Faker()
    private val orderStatuses = OrderStatus.entries.map { it.name }

    fun generateOrders(total: Int = 1000) {
        transaction {
            val userIds = Users.selectAll().map { it[Users.id] }
            val productIds = Products.selectAll().map { it[Products.id] to it[Products.brandId] }

            val startDate = LocalDate.of(2024, 1, 1)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(startDate, today).toInt()

            // 균등하게 분배
            val basePerDay = total / days
            val remainder = total % days

            val orderDateCounts = mutableListOf<Pair<LocalDate, Int>>()

            for (i in 0 until days) {
                val date = startDate.plusDays(i.toLong())
                orderDateCounts.add(date to basePerDay)
            }

            repeat(remainder) {
                val idx = (0 until days).random()
                orderDateCounts[idx] = orderDateCounts[idx].copy(second = orderDateCounts[idx].second + 1)
            }
            var totalCount = 0

            orderDateCounts.forEach { (date, count) ->
                repeat(count) {
                    val userId = userIds.random()
                    val orderedAt = JLocalDateTime.of(date, LocalTime.of((8..22).random(), (0..59).random())).toKotlinLocalDateTime()
                    val address = faker.address.fullAddress()
                    val status = orderStatuses.random()
                    val deliveryFee = (0..5000 step 500).toList().random()
                    val discount = listOf(0, 1000, 2000, 5000).random()

                    val productSelections = productIds.shuffled().take((1..3).random())
                    val totalAmount = productSelections.sumOf { (_, _) -> (10000..100000 step 500).toList().random() }

                    val orderId = Orders.insert {
                        it[Orders.userId] = userId
                        it[deliveryAddress] = address
                        it[Orders.status] = status
                        it[Orders.totalAmount] = totalAmount
                        it[discountAmount] = discount
                        it[Orders.deliveryFee] = deliveryFee
                        it[Orders.orderedAt] = orderedAt
                    } get Orders.id

                    productSelections.forEach { (productId, _) ->
                        OrderItems.insert {
                            it[OrderItems.orderId] = orderId
                            it[OrderItems.productId] = productId
                            it[quantity] = (1..5).random()
                        }
                    }

                    totalCount++
                }
            }

            println("주문 총 ${totalCount}건 생성 완료")
        }
    }
}
