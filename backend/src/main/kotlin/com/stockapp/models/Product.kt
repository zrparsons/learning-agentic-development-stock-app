package com.stockapp.models

import kotlinx.serialization.Serializable
import java.util.UUID
import java.time.LocalDateTime
import java.math.BigDecimal

data class Product(
    val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class ProductCreateRequest(
    val name: String,
    val description: String,
    val price: Double
)

@Serializable
data class ProductUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null
)

// Internal models for service layer
data class ProductCreateRequestInternal(
    val name: String,
    val description: String,
    val price: BigDecimal
)

data class ProductUpdateRequestInternal(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null
)

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val createdAt: String,
    val updatedAt: String
)

