package com.stockapp.services

import com.stockapp.database.Products
import com.stockapp.models.Product
import com.stockapp.models.ProductCreateRequestInternal
import com.stockapp.models.ProductUpdateRequestInternal
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ProductService {
    fun getAllProducts(): List<Product> {
        return transaction {
            Products.selectAll()
                .map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt]
                    )
                }
        }
    }
    
    fun getProductById(id: UUID): Product? {
        return transaction {
            Products.select { Products.id eq id }
                .map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt]
                    )
                }.firstOrNull()
        }
    }
    
    fun createProduct(request: ProductCreateRequestInternal): Result<Product> {
        return try {
            transaction {
                val productId = UUID.randomUUID()
                Products.insert {
                    it[Products.id] = productId
                    it[Products.name] = request.name
                    it[Products.description] = request.description
                    it[Products.price] = request.price
                }
                
                val product = Products.select { Products.id eq productId }.map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt]
                    )
                }.first()
                
                Result.success(product)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun updateProduct(id: UUID, request: ProductUpdateRequestInternal): Result<Product> {
        return try {
            transaction {
                val existingProduct = Products.select { Products.id eq id }.firstOrNull()
                
                if (existingProduct == null) {
                    return@transaction Result.failure(Exception("Product not found"))
                }
                
                Products.update({ Products.id eq id }) {
                    request.name?.let { name -> it[Products.name] = name }
                    request.description?.let { description -> it[Products.description] = description }
                    request.price?.let { price -> it[Products.price] = price }
                    it[Products.updatedAt] = LocalDateTime.now()
                }
                
                val product = Products.select { Products.id eq id }.map { row ->
                    Product(
                        id = row[Products.id],
                        name = row[Products.name],
                        description = row[Products.description],
                        price = row[Products.price],
                        createdAt = row[Products.createdAt],
                        updatedAt = row[Products.updatedAt]
                    )
                }.first()
                
                Result.success(product)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteProduct(id: UUID): Result<Unit> {
        return try {
            transaction {
                val deleted = Products.deleteWhere { Products.id eq id }
                
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

