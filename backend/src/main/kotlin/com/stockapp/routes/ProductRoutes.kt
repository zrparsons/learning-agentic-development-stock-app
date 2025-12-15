package com.stockapp.routes

import com.auth0.jwt.JWT
import com.stockapp.models.*
import com.stockapp.services.AuthService
import com.stockapp.services.ProductService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.productRoutes(productService: ProductService, authService: AuthService) {
    route("/api/products") {
        authenticate("auth-jwt") {
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userIdString = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw Exception("User ID not found in token")
                    val userId = UUID.fromString(userIdString)
                    
                    val products = productService.getAllProducts(userId)
                    call.respond(products.map { 
                        ProductResponse(
                            id = it.id.toString(),
                            name = it.name,
                            description = it.description,
                            price = it.price.toDouble(),
                            createdAt = it.createdAt.toString(),
                            updatedAt = it.updatedAt.toString(),
                            userId = it.userId.toString()
                        )
                    })
                } catch (e: Exception) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to fetch products: ${e.message}")
                    )
                }
            }
            
            get("{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userIdString = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw Exception("User ID not found in token")
                    val userId = UUID.fromString(userIdString)
                    
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val product = productService.getProductById(productId, userId)
                    if (product == null) {
                        call.respond(
                            io.ktor.http.HttpStatusCode.NotFound,
                            ErrorResponse("Product not found")
                        )
                        return@get
                    }
                    
                    call.respond(
                        ProductResponse(
                            id = product.id.toString(),
                            name = product.name,
                            description = product.description,
                            price = product.price.toDouble(),
                            createdAt = product.createdAt.toString(),
                            updatedAt = product.updatedAt.toString(),
                            userId = product.userId.toString()
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to fetch product: ${e.message}")
                    )
                }
            }
            
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userIdString = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw Exception("User ID not found in token")
                    val userId = UUID.fromString(userIdString)
                    
                    val request = call.receive<ProductCreateRequest>()
                    
                    if (request.name.isBlank() || request.description.isBlank()) {
                        call.respond(
                            io.ktor.http.HttpStatusCode.BadRequest,
                            ErrorResponse("Name and description are required")
                        )
                        return@post
                    }
                    
                    if (request.price < 0) {
                        call.respond(
                            io.ktor.http.HttpStatusCode.BadRequest,
                            ErrorResponse("Price must be non-negative")
                        )
                        return@post
                    }
                    
                    val createRequest = com.stockapp.models.ProductCreateRequestInternal(
                        name = request.name,
                        description = request.description,
                        price = java.math.BigDecimal.valueOf(request.price)
                    )
                    
                    val result = productService.createProduct(createRequest, userId)
                    result.fold(
                        onSuccess = { product ->
                            call.respond(
                                io.ktor.http.HttpStatusCode.Created,
                                ProductResponse(
                                    id = product.id.toString(),
                                    name = product.name,
                                    description = product.description,
                                    price = product.price.toDouble(),
                                    createdAt = product.createdAt.toString(),
                                    updatedAt = product.updatedAt.toString(),
                                    userId = product.userId.toString()
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                io.ktor.http.HttpStatusCode.BadRequest,
                                ErrorResponse(error.message ?: "Failed to create product")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request: ${e.message}")
                    )
                }
            }
            
            put("{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userIdString = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw Exception("User ID not found in token")
                    val userId = UUID.fromString(userIdString)
                    
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val request = call.receive<ProductUpdateRequest>()
                    
                    if (request.name?.isBlank() == true || request.description?.isBlank() == true) {
                        call.respond(
                            io.ktor.http.HttpStatusCode.BadRequest,
                            ErrorResponse("Name and description cannot be blank")
                        )
                        return@put
                    }
                    
                    if (request.price != null && request.price < 0) {
                        call.respond(
                            io.ktor.http.HttpStatusCode.BadRequest,
                            ErrorResponse("Price must be non-negative")
                        )
                        return@put
                    }
                    
                    val updateRequest = com.stockapp.models.ProductUpdateRequestInternal(
                        name = request.name,
                        description = request.description,
                        price = request.price?.let { java.math.BigDecimal.valueOf(it) }
                    )
                    
                    val result = productService.updateProduct(productId, updateRequest, userId)
                    result.fold(
                        onSuccess = { product ->
                            call.respond(
                                ProductResponse(
                                    id = product.id.toString(),
                                    name = product.name,
                                    description = product.description,
                                    price = product.price.toDouble(),
                                    createdAt = product.createdAt.toString(),
                                    updatedAt = product.updatedAt.toString(),
                                    userId = product.userId.toString()
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                io.ktor.http.HttpStatusCode.NotFound,
                                ErrorResponse(error.message ?: "Failed to update product")
                            )
                        }
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request: ${e.message}")
                    )
                }
            }
            
            delete("{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userIdString = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw Exception("User ID not found in token")
                    val userId = UUID.fromString(userIdString)
                    
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val result = productService.deleteProduct(productId, userId)
                    result.fold(
                        onSuccess = {
                            call.respond(io.ktor.http.HttpStatusCode.NoContent)
                        },
                        onFailure = { error ->
                            call.respond(
                                io.ktor.http.HttpStatusCode.NotFound,
                                ErrorResponse(error.message ?: "Failed to delete product")
                            )
                        }
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        io.ktor.http.HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to delete product: ${e.message}")
                    )
                }
            }
        }
    }
}

