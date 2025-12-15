package com.stockapp.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.LocalDateTime
import java.util.UUID

object Users : Table("users") {
    val id = uuid("id").primaryKey().default(UUID.randomUUID())
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = timestamp("created_at").default(LocalDateTime.now())
}

object Products : Table("products") {
    val id = uuid("id").primaryKey().default(UUID.randomUUID())
    val name = varchar("name", 255)
    val description = text("description")
    val price = decimal("price", 10, 2)
    val createdAt = timestamp("created_at").default(LocalDateTime.now())
    val updatedAt = timestamp("updated_at").default(LocalDateTime.now())
    val userId = uuid("user_id").references(Users.id)
}

