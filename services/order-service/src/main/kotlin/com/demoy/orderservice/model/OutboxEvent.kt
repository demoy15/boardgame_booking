package com.demoy.orderservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("outbox")
data class OutboxEvent(
    @Id
    val id: UUID? = null,
    val aggregateType: String,
    val aggregateId: UUID,
    val eventType: String,
    val payload: String,
    val published: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val publishedAt: LocalDateTime? = null
)