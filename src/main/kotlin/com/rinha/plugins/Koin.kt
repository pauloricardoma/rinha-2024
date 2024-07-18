package com.rinha.plugins

import com.rinha.repositories.CustomerRepository
import com.rinha.repositories.TransactionRepository
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule(environment))
    }
}

fun appModule(environment: ApplicationEnvironment) = module {
    single<CustomerRepository> { CustomerRepository() }
    single<TransactionRepository> { TransactionRepository() }
    single<HikariDataSource> { configureDatabase(environment) }
}