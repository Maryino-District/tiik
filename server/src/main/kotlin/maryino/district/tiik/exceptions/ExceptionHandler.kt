package maryino.district.tiik.exceptions

import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import maryino.district.tiik.models.ErrorResponse
import org.slf4j.LoggerFactory
private val logger = LoggerFactory.getLogger("ExceptionHandler")

fun Application.configureExceptionHandling() {
    install(StatusPages) {

        // 400 - Bad Request (клиентские ошибки)
        exception<BadRequestException> { call, cause ->
            logger.debug("Bad request: ${cause.message}")
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = HttpStatusCode.BadRequest.value,
                    message = cause.message ?: "Invalid request"
                )
            )
        }

        // 400 - Validation ошибки с деталями
        exception<ValidationException> { call, cause ->
            logger.debug("Validation error: ${cause.errors}")
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = HttpStatusCode.BadRequest.value,
                    message = cause.message ?: "Validation failed",
                    details = cause.errors
                )
            )
        }

        // 401 - Ошибки аутентификации
        exception<UnauthorizedException> { call, cause ->
            logger.info("Unauthorized: ${cause.message}")
            call.response.header("WWW-Authenticate", "Bearer")
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    code = HttpStatusCode.Unauthorized.value,
                    message = cause.message ?: "Authentication required"
                )
            )
        }

        // 403 - Доступ запрещён
        exception<ForbiddenException> { call, cause ->
            logger.info("Forbidden: ${cause.message}")
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorResponse(
                    code = HttpStatusCode.Forbidden.value,
                    message = cause.message ?: "Access denied"
                )
            )
        }

        // 404 - Ресурс не найден
        exception<NotFoundException> { call, cause ->
            logger.info("Not found: ${cause.message}")
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    code = HttpStatusCode.NotFound.value,
                    message = cause.message ?: "Resource not found"
                )
            )
        }

        // 409 - Конфликт
        exception<ConflictException> { call, cause ->
            logger.warn("Conflict: ${cause.message}")
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(
                    code = HttpStatusCode.Conflict.value,
                    message = cause.message ?: "Resource conflict"
                )
            )
        }

        // 500 - Неожиданные ошибки
        exception<Throwable> { call, cause ->
            val requestId = generateRequestId()
            logger.error("Internal error [RequestId: $requestId]", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    code = HttpStatusCode.InternalServerError.value,
                    message = "Internal server error",
                    details = mapOf("requestId" to requestId) // Только в dev среде
                )
            )
        }

        exception<JWTVerificationException> { call, cause ->
            logger.warn("JWT verification failed: ${cause.message}")
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    code = HttpStatusCode.Unauthorized.value,
                    message = "Invalid or expired token"
                )
            )
        }

        exception<NumberFormatException> { call, cause ->
            logger.warn("Invalid number format: ${cause.message}")
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = HttpStatusCode.BadRequest.value,
                    message = "Invalid parameter format",
                    details = mapOf("error" to (cause.message ?: "Expected a number"))
                )
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            logger.warn("Illegal argument: ${cause.message}")
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = HttpStatusCode.BadRequest.value,
                    message = cause.message ?: "Invalid request parameter"
                )
            )
        }
/*
        exception<Throwable> { call, cause ->
            // Инкрементим метрику ошибок
            metricsCounter.increment(call.request.path(), cause.javaClass.simpleName)

            logger.error("Internal server error", cause)
            call.respond(...)
        }*/ // crab
    }
}

// Вспомогательные исключения

fun generateRequestId(): String = UUID.randomUUID().toString()