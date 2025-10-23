package com.demoy.boardgamebooking.controller;


import com.demoy.boardgamebooking.dto.GameDto;
import com.demoy.boardgamebooking.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog")
public class BoardGameController {

    private final GameService service;

    @GetMapping("/games")
    public Flux<GameDto> games() {
        return service.getAll();
    }

    @PostMapping("/games")
    public Mono<Void> create(@RequestBody GameDto dto) {
        return service.create(dto);
    }

    @GetMapping("/games/{id}")
    public Mono<GameDto> getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}
