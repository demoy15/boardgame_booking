package com.demoy.orderservice.service

import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.model.OutboxEvent
import com.demoy.orderservice.model.RentalOrder
import com.demoy.orderservice.model.RentalOrderHold
import com.demoy.orderservice.repository.RentalOrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val orderRepo: RentalOrderRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val objectMapper: ObjectMapper,
    private val txOp: TransactionalOperator
) {

    fun createOrder(holdIds: List<UUID>, userId: UUID): Mono<UUID> {
        val orderId = UUID.randomUUID()
        val now = LocalDateTime.now()
        val order = RentalOrder(
            id = orderId,
            userId = userId,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal.ZERO,
            createdAt = now
        )

        val payload = objectMapper.writeValueAsString(
            mapOf(
                "orderId" to orderId.toString(),
                "holdIds" to holdIds.map { it.toString() },
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


        val saveSequence: Mono<Void> = r2dbcEntityTemplate.insert(order)
            .flatMap { savedOrder ->
                Flux.fromIterable(holdIds)
                    .map { holdId ->
                        RentalOrderHold(
                            id = UUID.randomUUID(),
                            orderId = savedOrder.id!!,
                            holdId = holdId
                        )
                    }
                    .flatMap { r2dbcEntityTemplate.insert(it) }
                    .then(Mono.just(savedOrder))
            }
            .flatMap { r2dbcEntityTemplate.insert(outbox) }
            .then()

        return txOp.execute { saveSequence }
            .then(Mono.just(orderId))
    }

    fun cancelOrder(orderId: UUID): Mono<Void> {
        return orderRepo.findById(orderId)
            .flatMap { order ->
                val holdsMono: Mono<List<UUID>> = r2dbcEntityTemplate.select(
                    Query.query(Criteria.where("order_id").`is`(orderId)),
                    RentalOrderHold::class.java
                )
                    .map { it.holdId }
                    .collectList()

                holdsMono.flatMap { holdIds ->
                    val canceled = order.copy(status = OrderStatus.CANCELLED)
                    val payload = objectMapper.writeValueAsString(
                        mapOf(
                            "orderId" to orderId.toString(),
                            "holdIds" to holdIds.map { it.toString() }
                        )
                    )
                    val outbox = OutboxEvent(
                        id = UUID.randomUUID(),
                        aggregateType = "rental_order",
                        aggregateId = orderId,
                        eventType = "order.cancelled",
                        payload = payload
                    )

                    val txWork = r2dbcEntityTemplate.update(canceled)
                        .flatMap { _ -> r2dbcEntityTemplate.insert(outbox) }
                        .then()

                    txOp.execute { txWork }.then()
                }
            }
            .switchIfEmpty(Mono.error(RuntimeException("order not found")))
    }
}
