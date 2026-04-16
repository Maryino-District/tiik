plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "maryino.district.tiik"
version = "1.0.0"
application {
    mainClass.set("maryino.district.tiik.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    // HikariCP — пул соединений, сразу готовые для того чтобы не тратить время на создание
    implementation(libs.hikaricp)
    // JDBC драйвер (пример для PostgreSQL)
    implementation(libs.postgresql)
    // хэшируем пароли
    implementation(libs.mindrot.jbcrypt)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    // orm
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.exposed.kotlin.datetime) // Для работы с kotlinx-datetime

    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}