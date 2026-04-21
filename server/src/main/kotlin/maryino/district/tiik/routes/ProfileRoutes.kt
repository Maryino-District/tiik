package maryino.district.tiik.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import maryino.district.tiik.db.Users
import maryino.district.tiik.models.ErrorResponse
import maryino.district.tiik.models.UpdateProfileRequest
import maryino.district.tiik.models.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Route.profileRoutes() {
    // 🔒 Защищённые маршруты (требуют токен)
    authenticate("auth-jwt") {
        rateLimit(RateLimitName("api-get-hard")) {
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
                        message = ErrorResponse("User not found", 404)
                    )
                } else {
                    call.respond(user)
                }
            }
        }

        rateLimit(RateLimitName("api-post")) {
            put("/api/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.subject

                val request = try {
                    call.receive<UpdateProfileRequest>()
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse("Invalid request format", 400)
                    )
                    return@put
                }

                val updated = transaction {
                    Users.update({ Users.id eq Uuid.parse(userId) }) {
                        it[email] = request.email.lowercase()
                    } > 0
                }

                if (!updated) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ErrorResponse("User not found", 404)
                    )
                } else {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Profile updated"))
                }
            }
        }

        rateLimit(RateLimitName("api-delete")) {
            delete("/api/delete") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.subject

                val deleted = transaction {
                    Users.deleteWhere { Users.id eq Uuid.parse(userId) }
                }

                if (deleted <= 0) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ErrorResponse("User not found", 404)
                    )
                } else {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Account deleted"))
                }
            }
        }
    }
}