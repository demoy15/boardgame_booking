package com.demoy.orderservice.controller

import com.demoy.orderservice.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

data class CreateOrderRequest(val holdId: String, val userId: String)
data class CreateOrderResponse(val orderId: String)

@RestController
@RequestMapping("/api/orders")
class OrderController(private val service: OrderService) {

    @PostMapping
    fun create(@RequestBody req: CreateOrderRequest): Mono<CreateOrderResponse> {
        val holdId = UUID.fromString(req.holdId)
        val userId = UUID.fromString(req.userId)
        return service.createOrder(holdId, userId).map { CreateOrderResponse(it.toString()) }
    }
}