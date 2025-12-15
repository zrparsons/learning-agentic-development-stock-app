package com.stockapp.models

import kotlinx.serialization.Serializable
import java.util.UUID
import java.time.LocalDateTime

data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: LocalDateTime
)

@Serializable
data class UserCreateRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String
)

