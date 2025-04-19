package bucket.generator

import bucket.domain.entity.Order
import bucket.domain.entity.OrderItem
import bucket.domain.model.OrderStatus
import bucket.domain.repository.*
import io.github.serpro69.kfaker.Faker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Component
class OrderGenerator(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository
) {
    private val faker = Faker()

    @Transactional
    fun generateOrders(total: Int = 1000) {
        val users = userRepository.findAll()
        val products = productRepository.findAll()
        val brands = brandRepository.findAll().associateBy { it.id }

        require(users.isNotEmpty()) { "사용자가 없습니다." }
        require(products.isNotEmpty()) { "상품이 없습니다." }

        val productInfos = products.map { it.id to it.brand.id }

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
                val user = users.random()
                val orderedAt = LocalDateTime.of(date, LocalTime.of((8..22).random(), (0..59).random()))
                val address = faker.address.fullAddress()
                val status = OrderStatus.entries.random()
                val discount = listOf(0, 1000, 2000, 5000).random()

                val selectedProducts = productInfos.shuffled().take((1..3).random()).map {
                    val quantity = (1..5).random()
                    Triple(it.first, it.second, quantity)
                }

                val productIdToProduct = productRepository.findAllById(selectedProducts.map { it.first }).associateBy { it.id }

                val totalAmount = selectedProducts.sumOf { (productId, _, quantity) ->
                    val unitPrice = productIdToProduct[productId]?.price ?: 0
                    unitPrice * quantity
                }

                val firstBrandId = selectedProducts.first().second
                val deliveryFee = brands[firstBrandId]?.deliveryFee ?: 3000

                val order = orderRepository.save(
                    Order(
                        user = user,
                        deliveryAddress = address,
                        status = status,
                        totalAmount = totalAmount,
                        discountAmount = discount,
                        deliveryFee = deliveryFee,
                        orderedAt = orderedAt
                    )
                )

                val orderItems = selectedProducts.map { (productId, _, quantity) ->
                    OrderItem(
                        order = order,
                        product = productIdToProduct[productId]!!,
                        quantity = quantity
                    )
                }

                orderItemRepository.saveAll(orderItems)
                totalCount++
            }
        }

        println("총 ${totalCount}건의 주문 생성 완료")
    }
}