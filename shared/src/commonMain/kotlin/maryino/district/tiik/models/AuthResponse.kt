package maryino.district.tiik.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val user: User,
    val token: String // JWT токен
)