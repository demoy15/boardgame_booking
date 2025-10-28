package com.demoy.boardbox_catalog.service;

import com.demoy.boardbox_catalog.dto.GameDto;
import com.demoy.boardbox_catalog.model.GameEntity;
import com.demoy.boardbox_catalog.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository repo;
    private final R2dbcEntityTemplate template;

    public Flux<GameDto> getAll() {
        return repo.findAll()
                .map(e -> new GameDto(e.getId(), e.getTitle(),
                        e.getMinPlayers() == null ? 0 : e.getMinPlayers(),
                        e.getMaxPlayers() == null ? 0 : e.getMaxPlayers(),
                        e.getPlaytimeMin() == null ? 0 : e.getPlaytimeMin()));
    }

    public Mono<Void> create(GameDto dto) {

        GameEntity e = new GameEntity(UUID.randomUUID(), dto.getTitle(),
                dto.getMinPlayers(), dto.getMaxPlayers(), dto.getPlaytimeMin());
        return template.insert(e).then();
    }

    public Mono<GameDto> getById(UUID id) {
        return repo.findById(id)
                .map(e -> new GameDto(e.getId(), e.getTitle(), e.getMinPlayers(), e.getMaxPlayers(), e.getPlaytimeMin()));
    }
}
