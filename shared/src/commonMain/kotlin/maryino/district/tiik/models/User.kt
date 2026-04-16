package maryino.district.tiik.models// shared-models/src/commonMain/kotlin/User.kt
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String, // UUID как строка для сериализации
    val email: String,
    val isVerified: Boolean,
    val createdAt: String // ISO 8601 строка
)




