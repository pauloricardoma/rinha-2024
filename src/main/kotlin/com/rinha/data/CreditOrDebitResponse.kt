package com.rinha.data

import kotlinx.serialization.Serializable

@Serializable
data class CreditOrDebitResponse(
    val saldo: Int,
    val limite: Int,
    val error: Boolean,
    val mensagem: String
)