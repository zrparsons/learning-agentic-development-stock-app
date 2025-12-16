package com.stockapp.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.stockapp.database.Users
import com.stockapp.models.AuthResponse
import com.stockapp.models.User
import com.stockapp.models.UserCreateRequest
import com.stockapp.models.UserLoginRequest
import com.stockapp.models.UserResponse
import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class AuthService {
    private val config = ConfigFactory.load()
    private val jwtSecret = config.getString("ktor.security.jwt.secret")
    private val jwtIssuer = config.getString("ktor.security.jwt.issuer")
    private val jwtAudience = config.getString("ktor.security.jwt.audience")
    
    fun register(request: UserCreateRequest): Result<User> {
        return try {
            transaction {
                // Check if user already exists
                val existingUser = Users.select { 
                    (Users.email eq request.email) or (Users.username eq request.username)
                }.firstOrNull()
                
                if (existingUser != null) {
                    return@transaction Result.failure(Exception("User with this email or username already exists"))
                }
                
                // Hash password
                val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
                
                // Create user
                val userId = UUID.randomUUID()
                Users.insert {
                    it[Users.id] = userId
                    it[Users.username] = request.username
                    it[Users.email] = request.email
                    it[Users.passwordHash] = passwordHash
                }
                
                val user = Users.select { Users.id eq userId }.map { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email],
                        passwordHash = row[Users.passwordHash],
                        createdAt = row[Users.createdAt]
                    )
                }.first()
                
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun login(request: UserLoginRequest): Result<AuthResponse> {
        return try {
            transaction {
                val userRow = Users.select { Users.email eq request.email }.firstOrNull()
                    ?: return@transaction Result.failure(Exception("Invalid email or password"))
                
                val user = User(
                    id = userRow[Users.id],
                    username = userRow[Users.username],
                    email = userRow[Users.email],
                    passwordHash = userRow[Users.passwordHash],
                    createdAt = userRow[Users.createdAt]
                )
                
                // Verify password
                if (!BCrypt.checkpw(request.password, user.passwordHash)) {
                    return@transaction Result.failure(Exception("Invalid email or password"))
                }
                
                // Generate JWT token
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .withClaim("userId", user.id.toString())
                    .withClaim("email", user.email)
                    .sign(Algorithm.HMAC256(jwtSecret))
                
                val authResponse = AuthResponse(
                    token = token,
                    user = UserResponse(
                        id = user.id.toString(),
                        username = user.username,
                        email = user.email
                    )
                )
                
                Result.success(authResponse)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getUserById(userId: UUID): User? {
        return transaction {
            Users.select { Users.id eq userId }.map { row ->
                User(
                    id = row[Users.id],
                    username = row[Users.username],
                    email = row[Users.email],
                    passwordHash = row[Users.passwordHash],
                    createdAt = row[Users.createdAt]
                )
            }.firstOrNull()
        }
    }
}

