package config

import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun init() {
        val env = dotenv {
            directory = "."          // .env 위치
            ignoreIfMissing = false
        }
        Database.connect(
            url = env["DB_URL"],
            driver = "com.mysql.cj.jdbc.Driver",
            user = env["DB_USER"],
            password = env["DB_PASSWORD"]
        )
    }
}