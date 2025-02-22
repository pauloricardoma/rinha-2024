package com.rinha

import com.rinha.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureKoin()
}