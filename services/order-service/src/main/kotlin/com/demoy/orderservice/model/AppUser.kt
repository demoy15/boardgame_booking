package com.demoy.orderservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("app_user")
data class AppUser(
    @Id
    val id: UUID? = null,
    val name: String,
    val email: String? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
