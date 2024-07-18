package com.rinha.data

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse (
    val limite: Int,
    val saldo: Int
)