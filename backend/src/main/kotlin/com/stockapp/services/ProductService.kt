package com.stockapp.services

import com.stockapp.database.Products
import com.stockapp.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class ProductService {
    fun getAllProducts(userId: UUID): List<Product> {
        return transaction {
            Products.select { Products.userId eq userId }
                .map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt],
                        userId = row[Products.userId]
                    )
                }
        }
    }
    
    fun getProductById(id: UUID, userId: UUID): Product? {
        return transaction {
            Products.select { 
                (Products.id eq id) and (Products.userId eq userId)
            }.map { row ->
                Product(
                    id = row[Products.id],
                    name = row[Products.name],
                    description = row[Products.description],
                    price = row[Products.price],
                    createdAt = row[Products.createdAt],
                    updatedAt = row[Products.updatedAt],
                    userId = row[Products.userId]
                )
            }.firstOrNull()
        }
    }
    
    fun createProduct(request: ProductCreateRequestInternal, userId: UUID): Result<Product> {
        return try {
            transaction {
                val productId = UUID.randomUUID()
                Products.insert {
                    it[id] = productId
                    it[name] = request.name
                    it[description] = request.description
                    it[price] = request.price
                    it[Products.userId] = userId
                }
                
                val product = Products.select { Products.id eq productId }.map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt],
                        userId = row[Products.userId]
                    )
                }.first()
                
                Result.success(product)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun updateProduct(id: UUID, request: ProductUpdateRequestInternal, userId: UUID): Result<Product> {
        return try {
            transaction {
                val existingProduct = Products.select { 
                    (Products.id eq id) and (Products.userId eq userId)
                }.firstOrNull()
                
                if (existingProduct == null) {
                    return@transaction Result.failure(Exception("Product not found"))
                }
                
                Products.update({ (Products.id eq id) and (Products.userId eq userId) }) {
                    request.name?.let { name -> it[Products.name] = name }
                    request.description?.let { description -> it[Products.description] = description }
                    request.price?.let { price -> it[Products.price] = price }
                    it[updatedAt] = LocalDateTime.now()
                }
                
                val product = Products.select { Products.id eq id }.map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt],
                        userId = row[Products.userId]
                    )
                }.first()
                
                Result.success(product)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteProduct(id: UUID, userId: UUID): Result<Unit> {
        return try {
            transaction {
                val deleted = Products.deleteWhere { 
                    (Products.id eq id) and (Products.userId eq userId)
                }
                
                if (deleted == 0) {
                    return@transaction Result.failure(Exception("Product not found"))
                }
                
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

