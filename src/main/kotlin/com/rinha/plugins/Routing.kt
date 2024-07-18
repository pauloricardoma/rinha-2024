package com.rinha.plugins

import com.rinha.routing.customerRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        customerRouting()
    }
}
