package maryino.district.tiik

import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import maryino.district.tiik.db.DatabaseFactory
import maryino.district.tiik.protections.JwtService
import maryino.district.tiik.protections.rateLimits
import maryino.district.tiik.routes.authRoutes
import maryino.district.tiik.routes.profileRoutes


fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    val jwtService = JwtService

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtService.getVerifier())
            validate { credential ->
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    install(RateLimit) {
        rateLimits()
    }

    routing {
        rateLimit(RateLimitName("global")) {
            get("/") {
                call.respondText("Ktor: ${Greeting().greet()}")
            }
            authRoutes()
            profileRoutes()
        }
    }
}