package com.demoy.orderservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Table("rental_order")
data class RentalOrder(
    @Id
    val id: UUID? = null,
    val userId: UUID,
    val status: OrderStatus,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val createdAt: LocalDateTime = LocalDateTime.now()
)