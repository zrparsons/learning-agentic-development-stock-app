plugins {
    kotlin("jvm") version "1.9.20"
    application
    kotlin("plugin.serialization") version "1.9.20"
}

group = "com.stockapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-auth:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.5")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
    implementation("org.postgresql:postgresql:42.6.0")
    
    // Database Migrations
    implementation("org.flywaydb:flyway-core:10.0.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.0.0")
    
    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // UUID
    implementation("com.fasterxml.uuid:java-uuid-generator:4.2.0")
    
    // JWT
    implementation("com.auth0:java-jwt:4.4.0")
}

application {
    mainClass.set("com.stockapp.ApplicationKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

