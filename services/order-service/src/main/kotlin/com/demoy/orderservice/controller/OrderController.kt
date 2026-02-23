package com.demoy.orderservice.controller

import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.service.OrderDetails
import com.demoy.orderservice.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

data class CreateOrderRequest(
    val holdIds: List<String>? = null,
    val holdId: String? = null,
    val userId: String
)

data class CreateOrderResponse(val orderId: String)

@RestController
@RequestMapping("/api/orders")
class OrderController(private val service: OrderService) {

    @PostMapping
    fun create(@RequestBody req: CreateOrderRequest): Mono<CreateOrderResponse> {
        val holdIds = when {
            !req.holdIds.isNullOrEmpty() -> req.holdIds
            !req.holdId.isNullOrBlank() -> listOf(req.holdId)
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "holdIds or holdId must be provided")
        }.map { UUID.fromString(it) }

        val userId = UUID.fromString(req.userId)
        return service.createOrder(holdIds, userId).map { CreateOrderResponse(it.toString()) }
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): Mono<OrderDetails> {
        val oid = UUID.fromString(orderId)
        return service.getOrderById(oid)
    }

    @GetMapping
    fun listOrders(
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) status: OrderStatus?
    ): Flux<OrderDetails> {
        return service.listOrders(userId?.let(UUID::fromString), status)
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: String): Mono<ResponseEntity<Void>> {
        val oid = UUID.fromString(orderId)
        return service.cancelOrder(oid)
            .then(Mono.fromCallable { ResponseEntity.noContent().build() })
    }
}
