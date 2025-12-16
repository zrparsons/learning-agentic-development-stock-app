package com.stockapp.database

import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

object FlywayConfig {
    fun migrate() {
        val config = ConfigFactory.load()
        val dbUrl = config.getString("ktor.database.url")
        val dbUser = config.getString("ktor.database.user")
        val dbPassword = config.getString("ktor.database.password")
        
        val flyway = Flyway.configure()
            .dataSource(dbUrl, dbUser, dbPassword)
            .locations("classpath:db/migration")
            .load()
        
        flyway.migrate()
    }
}

