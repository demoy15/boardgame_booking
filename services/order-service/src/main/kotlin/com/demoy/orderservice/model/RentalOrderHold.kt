package com.demoy.orderservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("rental_order_hold")
data class RentalOrderHold(
    @Id
    val id: UUID? = null,
    val orderId: UUID,
    val holdId: UUID
)