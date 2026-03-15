package com.demoy.boardbox_catalog.repository;

import com.demoy.boardbox_catalog.model.InventoryItemEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface InventoryItemRepository extends ReactiveCrudRepository<InventoryItemEntity, UUID> {
    Flux<InventoryItemEntity> findAllByGameId(UUID gameId);
}
