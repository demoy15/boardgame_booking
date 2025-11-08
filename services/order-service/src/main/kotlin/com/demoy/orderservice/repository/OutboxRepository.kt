package com.demoy.orderservice.repository

import com.demoy.orderservice.model.OutboxEvent
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.*

interface OutboxRepository : ReactiveCrudRepository<OutboxEvent, UUID> {
    fun findAllByPublishedFalse(): Flux<OutboxEvent>
}