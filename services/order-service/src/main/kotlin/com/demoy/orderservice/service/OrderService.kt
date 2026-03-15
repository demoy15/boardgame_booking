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

data class OrderDetails(
    val orderId: UUID,
    val userId: UUID,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val createdAt: LocalDateTime,
    val holdIds: List<UUID>
)

@Service
class OrderService(
    private val orderRepo: RentalOrderRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val objectMapper: ObjectMapper,
    private val txOp: TransactionalOperator,
    private val userService: UserService
) {

    fun createOrder(holdIds: List<UUID>, userId: UUID): Mono<UUID> {
        return userService.requireUser(userId).flatMap {
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

            txOp.execute { saveSequence }
                .then(Mono.just(orderId))
        }
    }

    fun getOrderById(orderId: UUID): Mono<OrderDetails> {
        return orderRepo.findById(orderId)
            .switchIfEmpty(Mono.error(NoSuchElementException("order not found")))
            .flatMap { toDetails(it) }
    }

    fun listOrders(userId: UUID?, status: OrderStatus?): Flux<OrderDetails> {
        var criteria: Criteria? = null
        if (userId != null) {
            criteria = Criteria.where("user_id").`is`(userId)
        }
        if (status != null) {
            val statusCriteria = Criteria.where("status").`is`(status)
            criteria = criteria?.and(statusCriteria) ?: statusCriteria
        }

        val query = criteria?.let { Query.query(it) } ?: Query.empty()

        return r2dbcEntityTemplate.select(query, RentalOrder::class.java)
            .flatMap { toDetails(it) }
    }

    fun cancelOrder(orderId: UUID): Mono<Void> {
        return orderRepo.findById(orderId)
            .flatMap { order ->
                val holdsMono: Mono<List<UUID>> = getHoldIds(orderId)

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
            .switchIfEmpty(Mono.error(NoSuchElementException("order not found")))
    }

    private fun toDetails(order: RentalOrder): Mono<OrderDetails> {
        val orderId = order.id ?: return Mono.error(RuntimeException("order id is missing"))
        return getHoldIds(orderId)
            .map { holdIds ->
                OrderDetails(
                    orderId = orderId,
                    userId = order.userId,
                    status = order.status,
                    totalAmount = order.totalAmount,
                    createdAt = order.createdAt,
                    holdIds = holdIds
                )
            }
    }

    private fun getHoldIds(orderId: UUID): Mono<List<UUID>> {
        return r2dbcEntityTemplate.select(
            Query.query(Criteria.where("order_id").`is`(orderId)),
            RentalOrderHold::class.java
        )
            .map { it.holdId }
            .collectList()
    }
}
