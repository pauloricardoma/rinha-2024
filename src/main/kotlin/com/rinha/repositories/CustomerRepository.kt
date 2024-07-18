package com.rinha.repositories

import com.rinha.models.Cliente
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.SQLException

class CustomerRepository: KoinComponent {
    private val dataSource by inject<HikariDataSource>()

    fun getById(id: Int): Cliente? {
        var cliente: Cliente? = null

        val connection = dataSource.connection

        try {
            val query = "SELECT id, nome, limite, saldo FROM clientes WHERE id = ?;"

            val stmt = connection.prepareStatement(query)
            stmt.setInt(1, id)

            val res = stmt.executeQuery()
            while (res.next()) {
                val id = res.getInt(1)
                val nome = res.getString(2)
                val limite = res.getInt(3)
                val saldo = res.getInt(4)

                cliente = Cliente(
                    id,
                    nome,
                    limite,
                    saldo
                )
            }

        } catch (e: SQLException) {
            throw e

        } finally {
            connection.close()
        }

        return cliente
    }
}