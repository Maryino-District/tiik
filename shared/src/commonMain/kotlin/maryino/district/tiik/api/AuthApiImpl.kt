// shared/src/commonMain/kotlin/com/example/shared/api/AuthApiImpl.kt
package maryino.district.tiik.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import maryino.district.tiik.models.ErrorResponse
import maryino.district.tiik.models.AuthResponse
import maryino.district.tiik.models.LoginRequest
import maryino.district.tiik.models.RegisterRequest
import maryino.district.tiik.models.User

class AuthApiImpl(
    private val baseUrl: String,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
) : AuthApi {
    
    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/api/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponse>()
                Result.Success(authResponse)
            } else {
                println("Error crab ${response.status.value}: ${response.bodyAsText()}")
                val error = response.body<ErrorResponse>()
                Result.Error(error.message, error.code)
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
    
    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/api/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponse>()
                Result.Success(authResponse)
            } else {
                val error = response.body<ErrorResponse>()
                Result.Error(error.message, error.code)
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getProfile(token: String): Result<User> {
        return try {
            val response = client.get("$baseUrl/api/profile") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            
            if (response.status.isSuccess()) {
                val user = response.body<User>()
                Result.Success(user)
            } else {
                Result.Error("Unauthorized", 401)
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}