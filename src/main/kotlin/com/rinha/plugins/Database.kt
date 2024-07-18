package com.rinha.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*

fun configureDatabase(environment: ApplicationEnvironment): HikariDataSource {
    val user = environment.config.property("ktor.db.dbUser").getString()
    val pass = environment.config.property("ktor.db.dbPassword").getString()
    val database = environment.config.property("ktor.db.dbDatabase").getString()
    val port = environment.config.property("ktor.db.dbPort").getString().toInt()
    val server = environment.config.property("ktor.db.dbServer").getString()

    val hkConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://$server:$port/$database"
        username = user
        password = pass
        addDataSourceProperty("cacheServerConfiguration", true)
        addDataSourceProperty("cachePrepStmts", "true")
        addDataSourceProperty("useUnbufferedInput", "false")
        addDataSourceProperty("prepStmtCacheSize", "4096")
        addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        maximumPoolSize = 4
        minimumIdle = 4
        validate()
    }

    val dataSource = HikariDataSource(hkConfig)

    return dataSource
}