package com.demoy.orderservice.service

import com.demoy.orderservice.dto.UserOrderDto
import com.demoy.orderservice.model.OrderStatus
import com.demoy.orderservice.model.UserOrder
import com.demoy.orderservice.repository.UserOrderRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class UserOrderService(
    private val userOrderRepository: UserOrderRepository
) {
    fun createUserOrder(order: UserOrder): Mono<UserOrderDto> {
        val savedOrder = userOrderRepository.save(order.copy(id = null))
        return Mono.just(toDto(savedOrder))
    }

    fun getUserOrderById(id: UUID): Mono<UserOrderDto> {
        val orderOpt = userOrderRepository.findById(id)
        return if (orderOpt.isPresent) {
            Mono.just(toDto(orderOpt.get()))
        } else {
            Mono.empty()
        }
    }

    fun getUserOrdersByUserId(userId: UUID): Mono<List<UserOrderDto>> {
        val orders = userOrderRepository.findByUserId(userId)
        return Mono.just(orders.map { toDto(it) })
    }

    fun getUserOrdersByUserIdAndStatus(userId: UUID, status: OrderStatus): Mono<List<UserOrderDto>> {
        val orders = userOrderRepository.findByUserIdAndStatus(userId, status)
        return Mono.just(orders.map { toDto(it) })
    }

    fun updateUserOrderStatus(orderId: UUID, status: OrderStatus): Mono<UserOrderDto> {
        val orderOpt = userOrderRepository.findById(orderId)
        return if (orderOpt.isPresent) {
            val order = orderOpt.get()
            val updatedOrder = userOrderRepository.save(
                order.copy(
                    status = status,
                    updatedAt = Instant.now()
                )
            )
            Mono.just(toDto(updatedOrder))
        } else {
            Mono.empty()
        }
    }

    fun cancelUserOrder(orderId: UUID): Mono<UserOrderDto> {
        return updateUserOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    private fun toDto(order: UserOrder): UserOrderDto {
        return UserOrderDto(
            id = order.id,
            items = order.items,
            status = order.status,
            userId = order.userId,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            completedAt = order.completedAt
        )
    }
}