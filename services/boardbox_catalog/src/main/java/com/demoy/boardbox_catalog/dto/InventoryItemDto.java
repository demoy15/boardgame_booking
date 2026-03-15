package com.demoy.boardbox_catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDto {
    private UUID id;
    private UUID gameId;
    private UUID branchId;
    private String status;
}
