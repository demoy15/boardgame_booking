package com.demoy.orderservice.dto

import com.demoy.orderservice.model.OrderItem
import com.demoy.orderservice.model.OrderStatus
import java.time.Instant
import java.util.*

data class UserOrderDto(
    val id: UUID?,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val userId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val completedAt: Instant?
)