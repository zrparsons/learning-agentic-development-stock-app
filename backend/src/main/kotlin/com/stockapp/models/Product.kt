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
    val stockCount: Int,
    val createdBy: UUID,
    val updatedBy: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class ProductCreateRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stockCount: Int = 0
)

@Serializable
data class ProductUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val stockCount: Int? = null
)

// Internal models for service layer
data class ProductCreateRequestInternal(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockCount: Int,
    val userId: UUID
)

data class ProductUpdateRequestInternal(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val stockCount: Int? = null,
    val userId: UUID
)

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stockCount: Int,
    val createdBy: String,
    val updatedBy: String,
    val createdAt: String,
    val updatedAt: String
)

