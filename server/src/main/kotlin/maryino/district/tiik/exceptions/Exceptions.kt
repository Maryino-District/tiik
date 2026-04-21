package maryino.district.tiik.exceptions

class ValidationException(message: String, val errors: Map<String, String>) : Exception(message)
//class UnauthorizedException(message: String) : Exception(message)
//class ForbiddenException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)
class RateLimitException(message: String, val retryAfter: Long) : Exception(message)
