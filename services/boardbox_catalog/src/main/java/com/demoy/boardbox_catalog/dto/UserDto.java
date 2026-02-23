package com.demoy.boardbox_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    @JsonProperty("id") UUID id,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("createdAt") LocalDateTime createdAt
) {}