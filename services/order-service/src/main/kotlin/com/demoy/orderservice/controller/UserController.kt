package com.demoy.orderservice.controller

import com.demoy.orderservice.model.AppUser
import com.demoy.orderservice.service.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

data class CreateUserRequest(
    val name: String,
    val email: String? = null
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String?,
    val createdAt: LocalDateTime
)

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun create(@RequestBody req: CreateUserRequest): Mono<UserResponse> {
        return userService.createUser(req.name, req.email)
            .map { it.toResponse() }
    }

    @GetMapping("/{userId}")
    fun get(@PathVariable userId: String): Mono<UserResponse> {
        val uid = UUID.fromString(userId)
        return userService.getUser(uid).map { it.toResponse() }
    }

    @GetMapping(params = ["email"])
    fun getByEmail(@RequestParam email: String): Mono<UserResponse> {
        return userService.getUserByEmail(email).map { it.toResponse() }
    }

    private fun AppUser.toResponse(): UserResponse =
        UserResponse(id = this.id.toString(), name = this.name, email = this.email, createdAt = this.createdAt)
}
