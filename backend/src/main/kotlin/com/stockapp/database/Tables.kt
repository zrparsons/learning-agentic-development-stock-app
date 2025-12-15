package com.stockapp.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.PrimaryKey
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.ReferenceOption
import java.time.LocalDateTime
import java.util.UUID

object Users : Table("users") {
    val id = uuid("id")
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    
    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = uuid("id")
    val name = varchar("name", 255)
    val description = text("description")
    val price = decimal("price", 10, 2)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(id)
}

