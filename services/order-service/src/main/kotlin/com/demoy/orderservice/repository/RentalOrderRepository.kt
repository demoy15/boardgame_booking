package com.demoy.orderservice.repository

import com.demoy.orderservice.model.RentalOrder
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RentalOrderRepository : ReactiveCrudRepository<RentalOrder, UUID>