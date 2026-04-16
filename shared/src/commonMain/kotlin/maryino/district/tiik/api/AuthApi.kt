package maryino.district.tiik.api

import maryino.district.tiik.models.AuthResponse
import maryino.district.tiik.models.LoginRequest
import maryino.district.tiik.models.RegisterRequest
import maryino.district.tiik.models.User

interface AuthApi {
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun getProfile(token: String): Result<User>
}