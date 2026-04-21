package maryino.district.tiik.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val code: Int,
    val details: Map<String, String>? = null
)