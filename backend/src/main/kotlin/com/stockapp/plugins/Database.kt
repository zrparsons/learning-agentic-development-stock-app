package com.stockapp.plugins

import com.stockapp.database.DatabaseConfig
import com.stockapp.database.Products
import com.stockapp.database.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    DatabaseConfig.init()
    
    transaction {
        SchemaUtils.create(Users, Products)
    }
}

