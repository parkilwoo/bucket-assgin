package generator

import io.github.serpro69.kfaker.Faker
import model.OrderStatus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import table.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime as JLocalDateTime

object OrderGenerator {
    private val faker = Faker()
    private val orderStatuses = OrderStatus.entries.map { it.name }

    fun generateOrders(total: Int = 1000) {
        transaction {
            val userIds = Users.selectAll().map { it[Users.id] }
            val productInfos = Products.selectAll().map { it[Products.id] to it[Products.brandId] }

            val startDate = LocalDate.of(2024, 1, 1)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(startDate, today).toInt()

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
                    val orderedAt = JLocalDateTime.of(date, LocalTime.of((8..22).random(), (0..59).random()))
                        .toKotlinLocalDateTime()
                    val address = faker.address.fullAddress()
                    val status = orderStatuses.random()
                    val discount = listOf(0, 1000, 2000, 5000).random()

                    // 상품 1~3개 선택
                    val selectedProducts = productInfos.shuffled().take((1..3).random())
                        .map { (productId, brandId) ->
                            val quantity = (1..5).random()
                            Triple(productId, brandId, quantity)
                        }

                    val productIds = selectedProducts.map { (productId, _, _) -> productId }

                    // 가격 조회 (slice + inList)
                    val prices = Products
                        .selectAll()
                        .filter { it[Products.id] in productIds }
                        .associate { it[Products.id] to it[Products.price] }

                    val totalAmount = selectedProducts.sumOf { (productId, _, quantity) ->
                        val unitPrice = prices[productId] ?: 0
                        unitPrice * quantity
                    }

                    // 배송비 조회 (slice + eq)
                    val firstBrandId: Long = selectedProducts.first().second
                    val deliveryFee = Brands
                        .selectAll()
                        .firstOrNull { it[Brands.id] == firstBrandId }
                        ?.get(Brands.deliveryFee) ?: 3000

                    val orderId = Orders.insert {
                        it[Orders.userId] = userId
                        it[deliveryAddress] = address
                        it[Orders.status] = status
                        it[Orders.totalAmount] = totalAmount
                        it[discountAmount] = discount
                        it[Orders.deliveryFee] = deliveryFee
                        it[Orders.orderedAt] = orderedAt
                    } get Orders.id

                    // 주문 항목 저장
                    selectedProducts.forEach { (productId, _, quantity) ->
                        OrderItems.insert {
                            it[OrderItems.orderId] = orderId
                            it[OrderItems.productId] = productId
                            it[OrderItems.quantity] = quantity
                        }
                    }

                    totalCount++
                }
            }

            println("총 ${totalCount}건의 주문 생성 완료")
        }
    }
}
