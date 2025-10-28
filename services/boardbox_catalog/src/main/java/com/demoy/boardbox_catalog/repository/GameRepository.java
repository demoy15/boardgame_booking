package com.demoy.boardbox_catalog.repository;

import com.demoy.boardbox_catalog.model.GameEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface GameRepository extends ReactiveCrudRepository<GameEntity, UUID> {
}
