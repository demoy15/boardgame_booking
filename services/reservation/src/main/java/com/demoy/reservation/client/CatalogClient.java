package com.demoy.reservation.client;

import com.demoy.reservation.dto.InventoryItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final WebClient catalogWebClient;

    public Flux<InventoryItemDto> getInventoryForGame(UUID gameId) {
        return catalogWebClient.get()
                .uri("/api/catalog/games/{id}/inventory", gameId)
                .retrieve()
                .bodyToFlux(InventoryItemDto.class);
    }
}
