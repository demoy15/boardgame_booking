package com.demoy.boardgamebooking.repository;

import com.demoy.boardgamebooking.model.GameEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface GameRepository extends ReactiveCrudRepository<GameEntity, UUID> {
}
