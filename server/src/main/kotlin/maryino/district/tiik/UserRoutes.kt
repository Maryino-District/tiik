package maryino.district.tiik

import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import maryino.district.tiik.models.ApiError
import maryino.district.tiik.models.AuthResponse
import maryino.district.tiik.models.LoginRequest
import maryino.district.tiik.models.RegisterRequest
import maryino.district.tiik.models.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Route.userRoutes() {

    // 🔓 Публичные маршруты (без проверки токена)

    // Регистрация
    post("/api/register") {
        val request = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ApiError("Invalid request format", 400)
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
                message = ApiError("Email already exists", 409)
            )
            return@post
        }

        // Создание пользователя
        val userId = transaction {
            Users.insert {
                it[email] = request.email.lowercase()
                it[passwordHash] = PasswordHasher.hash(request.password)
                it[isVerified] = false
            } [Users.id]
        }

        // Получение созданного пользователя
        val user = transaction {
            Users.selectAll()
                .where (Users.id eq userId )
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

    // Логин
    post("/api/login") {
        val request = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ApiError("Invalid request format", 400)
            )
            return@post
        }

        val dbUser = transaction {
            Users.selectAll()
                .where { Users.email eq request.email.lowercase() }
                .singleOrNull()
        }

        if (dbUser == null || !PasswordHasher.verify(request.password, dbUser[Users.passwordHash])) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApiError("Invalid credentials", 401)
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

    // 🔒 Защищённые маршруты (требуют токен)
    authenticate("auth-jwt") {
        get("/api/profile") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.subject

            val user = transaction {
                Users.selectAll()
                    .where { Users.id eq Uuid.parse(userId) }
                    .map { row ->
                        User(
                            id = row[Users.id].toString(),
                            email = row[Users.email],
                            isVerified = row[Users.isVerified],
                            createdAt = row[Users.createdAt].toString()
                        )
                    }
                    .singleOrNull()
            }

            if (user == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ApiError("User not found", 404)
                )
            } else {
                call.respond(user)
            }
        }

       /* put("/api/profile") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.subject

            val request = try {
                call.receive<UpdateProfileRequest>()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ApiError("Invalid request format", 400)
                )
                return@put
            }

            val updated = transaction {
                Users.update({ Users.id eq UUID.fromString(userId) }) {
                    it[email] = request.email.lowercase()
                } > 0
            }

            if (!updated) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ApiError("User not found", 404)
                )
            } else {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Profile updated"))
            }
        }*/

        delete("/api/account") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.subject

            val deleted = transaction {
                Users.deleteWhere { Users.id eq Uuid.parse(userId) }
            }

            if (deleted <= 0) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ApiError("User not found", 404)
                )
            } else {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Account deleted"))
            }
        }
    }
}