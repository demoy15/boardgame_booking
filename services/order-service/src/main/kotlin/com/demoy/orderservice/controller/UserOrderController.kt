package com.demoy.orderservice.controller

import com.demoy.orderservice.dto.UserOrderDto
import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.model.UserOrder
import com.demoy.orderservice.service.UserOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/user-orders")
class UserOrderController(
    private val userOrderService: UserOrderService
) {

    @PostMapping
    fun createUserOrder(@RequestBody order: UserOrder): Mono<UserOrderDto> {
        return userOrderService.createUserOrder(order)
    }

    @GetMapping("/{id}")
    fun getUserOrderById(@PathVariable id: UUID): Mono<UserOrderDto> {
        return userOrderService.getUserOrderById(id)
    }

    @GetMapping("/user/{userId}")
    fun getUserOrdersByUserId(@PathVariable userId: UUID): Mono<List<UserOrderDto>> {
        return userOrderService.getUserOrdersByUserId(userId)
    }

    @GetMapping("/user/{userId}/status/{status}")
    fun getUserOrdersByUserIdAndStatus(
        @PathVariable userId: UUID,
        @PathVariable status: OrderStatus
    ): Mono<List<UserOrderDto>> {
        return userOrderService.getUserOrdersByUserIdAndStatus(userId, status)
    }

    @PutMapping("/{id}/status")
    fun updateUserOrderStatus(
        @PathVariable id: UUID,
        @RequestParam status: OrderStatus
    ): Mono<UserOrderDto> {
        return userOrderService.updateUserOrderStatus(id, status)
    }

    @PostMapping("/{id}/cancel")
    fun cancelUserOrder(@PathVariable id: UUID): Mono<UserOrderDto> {
        return userOrderService.cancelUserOrder(id)
    }
}