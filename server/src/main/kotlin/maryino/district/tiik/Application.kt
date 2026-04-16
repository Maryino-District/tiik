package maryino.district.tiik

import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val jwtService = JwtService.apply {
        secret = System.getenv("JWT_SECRET") ?: "dev-secret"
        issuer = "my-mobile-app"
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

    routing {

        staticResources("/a", "", "sample.html")

        get("/") {
            staticResources("/", "", "sample.html")
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/user") {
            call.respondText {
                "hi!"
            }
        }

        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                call.respond(mapOf(
                    "userId" to principal.payload.subject,
                    "email" to principal.payload.getClaim("email").asString()
                ))
            }
        }
    }
}