package maryino.district.tiik.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val message: String,
    val code: Int
)