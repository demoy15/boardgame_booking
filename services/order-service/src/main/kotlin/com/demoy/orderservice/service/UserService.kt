package com.demoy.orderservice.service

import com.demoy.orderservice.model.AppUser
import com.demoy.orderservice.repository.UserRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val repo: UserRepository,
    private val template: R2dbcEntityTemplate
) {

    fun createUser(name: String, email: String?): Mono<AppUser> {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            return Mono.error(IllegalArgumentException("name is required"))
        }

        val normalizedEmail = email?.trim()?.takeIf { it.isNotBlank() }
        return if (normalizedEmail != null) {
            repo.findByEmail(normalizedEmail)
                .switchIfEmpty(
                    template.insert(AppUser(id = UUID.randomUUID(), name = trimmed, email = normalizedEmail))
                )
        } else {
            template.insert(AppUser(id = UUID.randomUUID(), name = trimmed))
        }
    }

    fun getUser(id: UUID): Mono<AppUser> {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(NoSuchElementException("user not found")))
    }

    fun getUserByEmail(email: String): Mono<AppUser> {
        val normalized = email.trim()
        if (normalized.isBlank()) {
            return Mono.error(IllegalArgumentException("email is required"))
        }
        return repo.findByEmail(normalized)
            .switchIfEmpty(Mono.error(NoSuchElementException("user not found")))
    }

    fun requireUser(id: UUID): Mono<AppUser> = getUser(id)
}
