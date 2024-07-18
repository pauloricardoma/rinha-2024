package com.rinha.data

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val valor: Int,
    val tipo: String,
    val descricao: String
)