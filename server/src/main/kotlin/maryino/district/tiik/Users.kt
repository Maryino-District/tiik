package maryino.district.tiik

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi

object Users : Table() {
    @OptIn(ExperimentalUuidApi::class)
    val id = uuid("id").clientDefault { Uuid.random() }
    val email = varchar("email", 254).uniqueIndex()  // 254, не 255
    val passwordHash = varchar("password_hash", 512)
    val isVerified = bool("is_verified").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    @OptIn(ExperimentalUuidApi::class)
    override val primaryKey = PrimaryKey(id)
}