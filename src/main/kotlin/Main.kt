import config.DatabaseConfig
import generator.BrandGenerator
import generator.OrderGenerator
import generator.ProductGenerator
import generator.UserAndLogLoader

fun main() {
    // DB 연결
    DatabaseConfig.init()
    UserAndLogLoader.loadUsersAndLogs("data/sample_user_log.json")
    BrandGenerator.generateBrands()
    ProductGenerator.generateProducts()
    OrderGenerator.generateOrders()
    println("모든 작업 완료")
}
