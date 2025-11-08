package com.demoy.orderservice.service

import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.model.OutboxEvent
import com.demoy.orderservice.model.RentalOrder
import com.demoy.orderservice.repository.OutboxRepository
import com.demoy.orderservice.repository.RentalOrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val orderRepo: RentalOrderRepository,
    private val outboxRepo: OutboxRepository,
    private val mapper: ObjectMapper,
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

        val payload = try {
            mapper.writeValueAsString(
                mapOf(
                    "orderId" to orderId.toString(),
                    "holdId" to holdId.toString(),
                    "userId" to userId.toString(),
                    "createdAt" to now.toString()
                )
            )
        } catch (e: Exception) {
            return Mono.error(RuntimeException("Failed to serialize payload", e))
        }

        val outbox = OutboxEvent(
            id = UUID.randomUUID(),
            aggregateType = "rental_order",
            aggregateId = orderId,
            eventType = "order.created",
            payload = payload
        )

        return txOp.execute { status ->
            orderRepo.save(order)
                .flatMap { savedOrder -> outboxRepo.save(outbox) }
                .then(Mono.just(orderId))
        }.next()
    }
}