package maryino.district.tiik.routes

import io.ktor.http.*
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import maryino.district.tiik.protections.JwtService
import maryino.district.tiik.protections.PasswordHasher
import maryino.district.tiik.db.Users
import maryino.district.tiik.models.ErrorResponse
import maryino.district.tiik.models.AuthResponse
import maryino.district.tiik.models.LoginRequest
import maryino.district.tiik.models.RegisterRequest
import maryino.district.tiik.models.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.authRoutes() {

    // 🔓 Публичные маршруты (без проверки токена)
        // Регистрация
    rateLimit(RateLimitName("auth")) {
        post("/api/register") {
            val request = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("Invalid request format", 400)
                )
                return@post
            }

            // Проверка существующего пользователя
            val existingUser = transaction {
                Users.selectAll()
                    .where { Users.email eq request.email.lowercase() }
                    .singleOrNull()
            }

            if (existingUser != null) {
                call.respond(
                    status = HttpStatusCode.Conflict,
                    message = ErrorResponse("Email already exists", 409)
                )
                return@post
            }

            // Создание пользователя
            val userId = transaction {
                Users.insert {
                    it[email] = request.email.lowercase()
                    it[passwordHash] = PasswordHasher.hash(request.password)
                    it[isVerified] = false
                }[Users.id]
            }

            // Получение созданного пользователя
            val user = transaction {
                Users.selectAll()
                    .where(Users.id eq userId)
                    .map { row ->
                        User(
                            id = row[Users.id].toString(),
                            email = row[Users.email],
                            isVerified = row[Users.isVerified],
                            createdAt = row[Users.createdAt].toString()
                        )
                    }
                    .single()
            }

            val token = JwtService.generateToken(user.id, user.email)

            call.respond(AuthResponse(user, token))
        }
    }

    rateLimit(RateLimitName("auth")) {
        // Логин
        post("/api/login") {
            val request = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("Invalid request format", 400)
                )
                return@post
            }

            val dbUser = transaction {
                Users.selectAll()
                    .where { Users.email eq request.email.lowercase() }
                    .singleOrNull()
            }

            if (dbUser == null || !PasswordHasher.verify(
                    request.password,
                    dbUser[Users.passwordHash]
                )
            ) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = ErrorResponse("Invalid credentials", 401)
                )
                return@post
            }

            val user = User(
                id = dbUser[Users.id].toString(),
                email = dbUser[Users.email],
                isVerified = dbUser[Users.isVerified],
                createdAt = dbUser[Users.createdAt].toString()
            )

            val token = JwtService.generateToken(user.id, user.email)

            call.respond(AuthResponse(user, token))
        }
    }

}