package com.rinha.models

import com.rinha.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Transacao(
    val id: Int,
    val clienteId: Int,
    val valor: Int,
    val tipo: String,
    val descricao: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val realizadaEm: LocalDateTime
)