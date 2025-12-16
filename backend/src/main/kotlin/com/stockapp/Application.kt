package com.stockapp

import com.stockapp.plugins.configureAuthentication
import com.stockapp.plugins.configureDatabase
import com.stockapp.plugins.configureRouting
import com.stockapp.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabase()
    configureAuthentication()
    configureRouting()
}

