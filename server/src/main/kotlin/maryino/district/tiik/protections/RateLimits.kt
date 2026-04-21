package maryino.district.tiik.protections

import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimitConfig
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration.Companion.minutes

fun RateLimitConfig.rateLimits() {
    register(RateLimitName("auth")) {
        rateLimiter(limit = 5, refillPeriod = 15.minutes)
        requestKey { call -> call.request.origin.remoteHost }
    }

    register(RateLimitName("api-get-light")) {
        rateLimiter(limit = 30, refillPeriod = 1.minutes)
        requestKey { call ->
            call.principal<JWTPrincipal>()?.subject ?: call.request.origin.remoteHost

        }
    }

    register(RateLimitName("api-get-hard")) {
        rateLimiter(limit = 15, refillPeriod = 1.minutes)
        requestKey { call ->
            call.principal<JWTPrincipal>()?.subject ?: call.request.origin.remoteHost

        }
    }

    register(RateLimitName("api-post")) {
        rateLimiter(limit = 15, refillPeriod = 1.minutes)
        requestKey { call ->
            call.principal<JWTPrincipal>()?.subject ?: call.request.origin.remoteHost

        }
    }

    register(RateLimitName("api-delete")) {
        rateLimiter(limit = 10, refillPeriod = 1.minutes)
        requestKey { call ->
            call.principal<JWTPrincipal>()?.subject ?: call.request.origin.remoteHost
        }
    }

    register(RateLimitName("global")) {
        rateLimiter(limit = 200, refillPeriod = 1.minutes)
        requestKey { call -> call.request.origin.remoteHost }
    }
}