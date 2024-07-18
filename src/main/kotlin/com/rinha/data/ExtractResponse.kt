package com.rinha.data

import com.rinha.models.Cliente
import com.rinha.models.Transacao
import com.rinha.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ExtractBalance (
    val total: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val data_extrato: LocalDateTime,
    val limite: Int
)

@Serializable
data class ExtractTransaction (
    val valor: Int,
    val tipo: String,
    val descricao: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val realizada_em: LocalDateTime
)

@Serializable
data class ExtractResponse (
    val saldo: ExtractBalance,
    val ultimas_transacoes: List<ExtractTransaction>,
) {
    companion object {
        fun populated(customer: Cliente, transactions: List<Transacao>): ExtractResponse {
            val extract = ExtractResponse(
                saldo = ExtractBalance(customer.saldo, LocalDateTime.now(), customer.limite),
                ultimas_transacoes = transactions.map {transaction ->
                    ExtractTransaction(
                        transaction.valor,
                        transaction.tipo,
                        transaction.descricao,
                        transaction.realizadaEm
                    )
                }
            )
            return extract
        }
    }
}