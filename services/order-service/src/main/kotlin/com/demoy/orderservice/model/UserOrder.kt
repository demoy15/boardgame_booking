package com.demoy.orderservice.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "user_orders")
data class UserOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    val items: List<OrderItem>,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column
    val updatedAt: Instant? = null,

    @Column
    val completedAt: Instant? = null
)

data class OrderItem(
    val gameId: UUID,
    val title: String,
    val quantity: Int,
    val price: Double
)