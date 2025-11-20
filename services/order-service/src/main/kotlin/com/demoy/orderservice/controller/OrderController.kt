package com.demoy.orderservice.controller

import com.demoy.orderservice.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

data class CreateOrderRequest(val holdIds: List<String>, val userId: String)
data class CreateOrderResponse(val orderId: String)

@RestController
@RequestMapping("/api/orders")
class OrderController(private val service: OrderService) {

    @PostMapping
    fun create(@RequestBody req: CreateOrderRequest): Mono<CreateOrderResponse> {
        val holdIds = req.holdIds.map { UUID.fromString(it) }
        val userId = UUID.fromString(req.userId)
        return service.createOrder(holdIds, userId).map { CreateOrderResponse(it.toString()) }
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: String): Mono<ResponseEntity<Void>> {
        val oid = UUID.fromString(orderId)
        return service.cancelOrder(oid)
            .then(Mono.fromCallable { ResponseEntity.noContent().build() })
    }
}