package maryino.district.tiik

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtService {

    lateinit var secret: String
    lateinit var issuer: String

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