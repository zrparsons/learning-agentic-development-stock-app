package com.stockapp.routes

import com.stockapp.models.ErrorResponse
import com.stockapp.models.UserResponse
import com.stockapp.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.userRoutes(authService: AuthService) {
    route("/api/users") {
        authenticate("auth-jwt") {
            get("{id}") {
                try {
                    val id = call.parameters["id"] ?: throw Exception("User ID is required")
                    val userId = UUID.fromString(id)
                    
                    val user = authService.getUserById(userId)
                    if (user == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("User not found")
                        )
                        return@get
                    }
                    
                    call.respond(
                        UserResponse(
                            id = user.id.toString(),
                            username = user.username,
                            email = user.email
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid user ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to fetch user: ${e.message}")
                    )
                }
            }
        }
    }
}
