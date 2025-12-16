package com.stockapp.routes

import com.stockapp.models.AuthResponse
import com.stockapp.models.ErrorResponse
import com.stockapp.models.UserCreateRequest
import com.stockapp.models.UserLoginRequest
import com.stockapp.models.UserResponse
import com.stockapp.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/api/auth") {
        post("/register") {
            try {
                val request = call.receive<UserCreateRequest>()
                
                if (request.username.isBlank() || request.email.isBlank() || request.password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Username, email, and password are required")
                    )
                    return@post
                }
                
                val result = authService.register(request)
                result.fold(
                    onSuccess = { user ->
                        call.respond(
                            HttpStatusCode.Created,
                            UserResponse(
                                id = user.id.toString(),
                                username = user.username,
                                email = user.email
                            )
                        )
                    },
                    onFailure = { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error.message ?: "Registration failed")
                        )
                    }
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request: ${e.message}")
                )
            }
        }
        
        post("/login") {
            try {
                val request = call.receive<UserLoginRequest>()
                
                if (request.email.isBlank() || request.password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Email and password are required")
                    )
                    return@post
                }
                
                val result = authService.login(request)
                result.fold(
                    onSuccess = { authResponse ->
                        val response = AuthResponse(
                            token = authResponse.token,
                            user = UserResponse(
                                id = authResponse.user.id.toString(),
                                username = authResponse.user.username,
                                email = authResponse.user.email
                            )
                        )
                        call.respond(HttpStatusCode.OK, response)
                    },
                    onFailure = { error ->
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(error.message ?: "Login failed")
                        )
                    }
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request: ${e.message}")
                )
            }
        }
    }
}

