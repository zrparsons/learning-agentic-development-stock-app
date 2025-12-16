package com.stockapp.plugins

import com.stockapp.routes.authRoutes
import com.stockapp.routes.productRoutes
import com.stockapp.routes.userRoutes
import com.stockapp.services.AuthService
import com.stockapp.services.ProductService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val authService = AuthService()
    val productService = ProductService()
    
    routing {
        authRoutes(authService)
        productRoutes(productService)
        userRoutes(authService)
    }
}

