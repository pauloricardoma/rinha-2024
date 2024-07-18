package com.rinha.repositories

import com.rinha.data.CreditOrDebitResponse
import com.rinha.data.TransactionRequest
import com.rinha.models.Transacao
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.SQLException

class TransactionRepository: KoinComponent {
    private val dataSource by inject<HikariDataSource>()

    fun creditOrDebit(customerId: Int, transaction: TransactionRequest): CreditOrDebitResponse? {
        val type = transaction.tipo
        var creditOrDebitResponse: CreditOrDebitResponse? = null

        val connection = dataSource.connection

        try {
            var query = ""
            if (type == "d") {
                query = "SELECT novo_saldo, novo_limite, error, mensagem FROM debit(?, ?, ?);"
            }
            if (type == "c") {
                query = "SELECT novo_saldo, novo_limite, error, mensagem FROM credit(?, ?, ?);"
            }

            val stmt = connection.prepareStatement(query)
            stmt.setInt(1, customerId)
            stmt.setInt(2, transaction.valor)
            stmt.setString(3, transaction.descricao)

            val res = stmt.executeQuery()
            while (res.next()) {
                val novoSaldo = res.getInt(1)
                val novoLimite = res.getInt(2)
                val error = res.getBoolean(3)
                val mensagem = res.getString(4)

                creditOrDebitResponse = CreditOrDebitResponse(
                    novoLimite,
                    novoSaldo,
                    error,
                    mensagem
                )
            }

        } catch (e: SQLException) {
            throw e

        } finally {
            connection.close()
        }

        return creditOrDebitResponse
    }

    fun listLastByCustomerId(clienteId: Int, limit: Int): List<Transacao> {
        var lastTransactions = mutableListOf<Transacao>()

        val connection = dataSource.connection

        try {
            val query = """
                SELECT id, cliente_id, valor, tipo, descricao, realizada_em 
                    FROM transacoes WHERE cliente_id = ? 
                    ORDER BY realizada_em DESC
                    LIMIT ?;
            """.trimIndent()

            val stmt = connection.prepareStatement(query)
            stmt.setInt(1, clienteId)
            stmt.setInt(2, limit)

            val res = stmt.executeQuery()
            while (res.next()) {
                val rowId = res.getInt(1)
                val rowClienteId = res.getInt(2)
                val rowValor = res.getInt(3)
                val rowTipo = res.getString(4)
                val rowDescricao = res.getString(5)
                val rowRealizadaEm = res.getTimestamp(6).toLocalDateTime()

                lastTransactions.add(Transacao(
                    rowId,
                    rowClienteId,
                    rowValor,
                    rowTipo,
                    rowDescricao,
                    rowRealizadaEm
                ))

            }

        } catch (e: SQLException) {
            throw e

        } finally {
            connection.close()
        }

        return lastTransactions
    }
}