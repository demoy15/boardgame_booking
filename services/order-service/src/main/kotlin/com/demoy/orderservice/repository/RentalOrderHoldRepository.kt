package com.demoy.orderservice.repository

import com.demoy.orderservice.model.RentalOrderHold
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface RentalOrderHoldRepository : ReactiveCrudRepository<RentalOrderHold, UUID> {
    fun findAllByOrderId(orderId: UUID): Flux<RentalOrderHold>
    fun findAllByHoldId(holdId: UUID): Flux<RentalOrderHold>
}