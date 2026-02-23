package com.demoy.orderservice.repository

import com.demoy.orderservice.model.UserOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserOrderRepository : JpaRepository<UserOrder, UUID> {
    fun findByUserId(userId: UUID): List<UserOrder>
    fun findByUserIdAndStatus(userId: UUID, status: com.demoy.orderservice.model.OrderStatus): List<UserOrder>
}