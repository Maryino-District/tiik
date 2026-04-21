package maryino.district.tiik.protections

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtService {

    var secret: String = System.getenv("JWT_SECRET") ?: "dev-secret"

    var issuer: String = "my-mobile-app"

    private val algorithm = Algorithm.HMAC256(secret)
    
    fun generateToken(userId: String, email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    }
    
    fun getVerifier() = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()
}