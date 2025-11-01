package com.demoy.reservation.controller;

import com.demoy.reservation.dto.HoldRequest;
import com.demoy.reservation.dto.HoldResponse;
import com.demoy.reservation.service.HoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final HoldService holdService;

    @PostMapping(value = "/holds", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<HoldResponse> createHold(@RequestBody HoldRequest req) {
        return holdService.createHold(req);
    }

    @GetMapping("/holds/{holdId}")
    public Mono<HoldResponse> getHold(@PathVariable String holdId) {
        return holdService.getHoldById(holdId);
    }

    @DeleteMapping("/holds/{holdId}")
    public Mono<Void> cancelHold(@PathVariable String holdId) {
        return holdService.cancelHold(holdId);
    }
}
