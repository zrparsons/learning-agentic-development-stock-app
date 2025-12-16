package com.stockapp.routes

import com.stockapp.models.ErrorResponse
import com.stockapp.models.ProductCreateRequest
import com.stockapp.models.ProductResponse
import com.stockapp.models.ProductUpdateRequest
import com.stockapp.services.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.productRoutes(productService: ProductService) {
    route("/api/products") {
        authenticate("auth-jwt") {
            get {
                try {
                    val products = productService.getAllProducts()
                    call.respond(products.map { 
                        ProductResponse(
                            id = it.id.toString(),
                            name = it.name,
                            description = it.description,
                            price = it.price.toDouble(),
                            stockCount = it.stockCount,
                            createdAt = it.createdAt.toString(),
                            updatedAt = it.updatedAt.toString()
                        )
                    })
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to fetch products: ${e.message}")
                    )
                }
            }
            
            get("{id}") {
                try {
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val product = productService.getProductById(productId)
                    if (product == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
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
                            stockCount = product.stockCount,
                            createdAt = product.createdAt.toString(),
                            updatedAt = product.updatedAt.toString()
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to fetch product: ${e.message}")
                    )
                }
            }
            
            post {
                try {
                    val request = call.receive<ProductCreateRequest>()
                    
                    if (request.name.isBlank() || request.description.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Name and description are required")
                        )
                        return@post
                    }
                    
                    if (request.price < 0) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Price must be non-negative")
                        )
                        return@post
                    }
                    
                    if (request.stockCount < 0) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Stock count must be non-negative")
                        )
                        return@post
                    }
                    
                    val createRequest = com.stockapp.models.ProductCreateRequestInternal(
                        name = request.name,
                        description = request.description,
                        price = java.math.BigDecimal.valueOf(request.price),
                        stockCount = request.stockCount
                    )
                    
                    val result = productService.createProduct(createRequest)
                    result.fold(
                        onSuccess = { product ->
                            call.respond(
                                HttpStatusCode.Created,
                                ProductResponse(
                                    id = product.id.toString(),
                                    name = product.name,
                                    description = product.description,
                                    price = product.price.toDouble(),
                                    stockCount = product.stockCount,
                                    createdAt = product.createdAt.toString(),
                                    updatedAt = product.updatedAt.toString()
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ErrorResponse(error.message ?: "Failed to create product")
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
            
            put("{id}") {
                try {
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val request = call.receive<ProductUpdateRequest>()
                    
                    if (request.name?.isBlank() == true || request.description?.isBlank() == true) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Name and description cannot be blank")
                        )
                        return@put
                    }
                    
                    if (request.price != null && request.price < 0) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Price must be non-negative")
                        )
                        return@put
                    }
                    
                    if (request.stockCount != null && request.stockCount < 0) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Stock count must be non-negative")
                        )
                        return@put
                    }
                    
                    val updateRequest = com.stockapp.models.ProductUpdateRequestInternal(
                        name = request.name,
                        description = request.description,
                        price = request.price?.let { java.math.BigDecimal.valueOf(it) },
                        stockCount = request.stockCount
                    )
                    
                    val result = productService.updateProduct(productId, updateRequest)
                    result.fold(
                        onSuccess = { product ->
                            call.respond(
                                ProductResponse(
                                    id = product.id.toString(),
                                    name = product.name,
                                    description = product.description,
                                    price = product.price.toDouble(),
                                    stockCount = product.stockCount,
                                    createdAt = product.createdAt.toString(),
                                    updatedAt = product.updatedAt.toString()
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(error.message ?: "Failed to update product")
                            )
                        }
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request: ${e.message}")
                    )
                }
            }
            
            delete("{id}") {
                try {
                    val id = call.parameters["id"] ?: throw Exception("Product ID is required")
                    val productId = UUID.fromString(id)
                    
                    val result = productService.deleteProduct(productId)
                    result.fold(
                        onSuccess = {
                            call.respond(HttpStatusCode.NoContent)
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(error.message ?: "Failed to delete product")
                            )
                        }
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid product ID: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Failed to delete product: ${e.message}")
                    )
                }
            }
        }
    }
}

