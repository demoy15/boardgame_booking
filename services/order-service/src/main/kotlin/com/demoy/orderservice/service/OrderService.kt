package com.demoy.orderservice.service

import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.model.OutboxEvent
import com.demoy.orderservice.model.RentalOrder
import com.demoy.orderservice.repository.RentalOrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val orderRepo: RentalOrderRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val objectMapper: ObjectMapper,
    private val txOp: TransactionalOperator
) {

    fun createOrder(holdId: UUID, userId: UUID): Mono<UUID> {
        val orderId = UUID.randomUUID()
        val now = LocalDateTime.now()
        val order = RentalOrder(
            id = orderId,
            userId = userId,
            holdId = holdId,
            status = OrderStatus.PENDING,
            createdAt = now
        )

        val payload = objectMapper.writeValueAsString(
            mapOf(
                "orderId" to orderId.toString(),
                "holdId" to holdId.toString(),
                "userId" to userId.toString(),
                "createdAt" to now.toString()
            )
        )

        val outbox = OutboxEvent(
            id = UUID.randomUUID(),
            aggregateType = "rental_order",
            aggregateId = orderId,
            eventType = "order.created",
            payload = payload
        )

        return txOp.execute {
            r2dbcEntityTemplate.insert<RentalOrder>(order)
                .flatMap { r2dbcEntityTemplate.insert<OutboxEvent>(outbox) }.then()
        }.then(Mono.just(orderId))
    }


    fun cancelOrder(orderId: UUID): Mono<Void> {
        return orderRepo.findById(orderId)
            .flatMap { order ->
                val canceled = order.copy(status = OrderStatus.CANCELLED)
                txOp.execute {
                    r2dbcEntityTemplate.update(canceled)
                        .flatMap {
                            val payload = objectMapper.writeValueAsString(
                                mapOf(
                                    "orderId" to orderId.toString(),
                                    "holdId" to order.holdId.toString()
                                )
                            )
                            val outbox = OutboxEvent(
                                id = UUID.randomUUID(),
                                aggregateType = "rental_order",
                                aggregateId = orderId,
                                eventType = "order.cancelled",
                                payload = payload
                            )
                            r2dbcEntityTemplate.insert<OutboxEvent>(outbox)
                        }
                }.then()
            }
            .switchIfEmpty(Mono.error(RuntimeException("order not found")))
    }
}