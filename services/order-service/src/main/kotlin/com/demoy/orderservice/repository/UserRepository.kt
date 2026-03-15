package com.demoy.orderservice.repository

import com.demoy.orderservice.model.AppUser
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserRepository : ReactiveCrudRepository<AppUser, UUID> {
    fun findByEmail(email: String): Mono<AppUser>
}
