package com.demoy.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldRequest {
    private UUID inventoryId;
    private LocalDateTime from;
    private LocalDateTime to;
    private UUID userId;
}
