package maryino.district.tiik.db

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:postgresql://postgres-db:5432/ktor_db",
            driver = "org.postgresql.Driver",
            user = "ktor_user",
            password = "ktor_password"
        )
        
        transaction {
            SchemaUtils.create(Users)
        }
    }
}