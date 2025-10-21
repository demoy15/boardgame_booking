package com.demoy.boardgamebooking.controller;


import com.demoy.boardgamebooking.model.Game;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
public class BoardGameController {


    @GetMapping("/games")
    public Flux<Game> games() {
        List<Game> demo = List.of(
                new Game(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Catan", 3, 4, 90),
                new Game(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Wingspan", 1, 5, 80),
                new Game(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Ticket to Ride", 2, 5, 60)
        );
        return Flux.fromIterable(demo);
    }
}
