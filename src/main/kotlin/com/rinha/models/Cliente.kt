package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class Cliente(
    val id: Int,
    val nome: String,
    val limite: Int,
    val saldo: Int
)