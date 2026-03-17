package maryino.district.tiik

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform