package com.stockapp.database

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun init() {
        val config = ConfigFactory.load()
        val dbUrl = config.getString("ktor.database.url")
        val dbUser = config.getString("ktor.database.user")
        val dbPassword = config.getString("ktor.database.password")
        val dbDriver = config.getString("ktor.database.driver")
        
        Database.connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )
    }
}

