package maryino.district.tiik.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String // приходит от клиента, НЕ хранится в ответах
)