package com.stockapp.plugins

import com.stockapp.database.DatabaseConfig
import com.stockapp.database.FlywayConfig
import io.ktor.server.application.Application

fun Application.configureDatabase() {
    DatabaseConfig.init()
    FlywayConfig.migrate()
}

