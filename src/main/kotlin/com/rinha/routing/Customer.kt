package com.rinha.routing

import com.rinha.data.ExtractResponse
import com.rinha.data.TransactionRequest
import com.rinha.data.TransactionResponse
import com.rinha.repositories.CustomerRepository
import com.rinha.repositories.TransactionRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.customerRouting() {
    val customerRepository by inject<CustomerRepository>()
    val transactionRepository by inject<TransactionRepository>()

    route("/clientes/{id}") {
        get("/extrato") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respondText("Parâmetro inválido!", status = HttpStatusCode.UnprocessableEntity)

            val customer = customerRepository.getById(id)
                ?: return@get call.respondText("Cliente não encontrado!", status = HttpStatusCode.NotFound)

            val lastTransactions = transactionRepository.listLastByCustomerId(id, 10)

            val extractResponse = ExtractResponse.populated(customer, lastTransactions)
            call.respond(HttpStatusCode.OK, extractResponse)
        }

        post("/transacoes") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@post call.respondText("Parâmetro inválido!", status = HttpStatusCode.UnprocessableEntity)

            val body = call.runCatching {
                receive<TransactionRequest>()
            }.getOrElse {
                return@post call.respondText("Parâmetros inválidos!", status = HttpStatusCode.UnprocessableEntity)
            }

            if (body.tipo != "c" && body.tipo != "d") {
                return@post call.respondText("Parâmetros inválidos!", status = HttpStatusCode.UnprocessableEntity)
            }

            if (body.descricao.isEmpty() || body.descricao.length > 10) {
                return@post call.respondText("Parâmetros inválidos!", status = HttpStatusCode.UnprocessableEntity)
            }

            val transaction = transactionRepository.creditOrDebit(id, body)
                ?: return@post call.respondText("Cliente não encontrado!", status = HttpStatusCode.NotFound)

            if (transaction.error)
                return@post call.respondText(transaction.mensagem, status = HttpStatusCode.UnprocessableEntity)

            val transactionResponse = TransactionResponse(
                transaction.limite,
                transaction.saldo
            )
            call.respond(HttpStatusCode.OK, transactionResponse)
        }
    }
}