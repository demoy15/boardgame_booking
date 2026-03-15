package com.demoy.reservation.service;

import com.demoy.reservation.client.CatalogClient;
import com.demoy.reservation.dto.HoldRequest;
import com.demoy.reservation.dto.HoldResponse;
import com.demoy.reservation.dto.InventoryItemDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HoldService {

    private final ReactiveStringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final com.demoy.reservation.messaging.EventPublisher eventPublisher;
    private final CatalogClient catalogClient;

    @Value("${reservation.hold.ttl-seconds:900}")
    private long ttlSeconds;

    // key patterns
    private String inventoryKey(UUID inventoryId, LocalDateTime from, LocalDateTime to) {
        return "inventory-hold:" + inventoryId + ":" + from.toString() + ":" + to.toString();
    }

    private String holdKey(UUID holdId) {
        return "hold:" + holdId.toString();
    }

    public Mono<HoldResponse> createHold(HoldRequest req) {
        if (req.getInventoryId() != null) {
            return createHoldForInventory(req.getInventoryId(), req);
        }
        if (req.getGameId() != null) {
            return createHoldForGame(req);
        }
        return Mono.error(new IllegalArgumentException("inventoryId or gameId is required"));
    }

    public Mono<HoldResponse> getHoldById(String holdIdStr) {
        String key = "hold:" + holdIdStr;
        return redis.opsForValue().get(key)
                .flatMap(json -> {
                    try {
                        HoldRecord r = mapper.readValue(json, HoldRecord.class);
                        return Mono.just(new HoldResponse(r.getHoldId(), r.getExpiresAt(), "HELD"));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    public Mono<Void> cancelHold(String holdIdStr) {
        String hk = "hold:" + holdIdStr;
        return redis.opsForValue().get(hk)
                .flatMap(json -> {
                    try {
                        HoldRecord r = mapper.readValue(json, HoldRecord.class);
                        String invKey = inventoryKey(r.getInventoryId(), r.getFrom(), r.getTo());
                        return redis.opsForValue().getAndDelete(hk)
                                .then(redis.opsForValue().getAndDelete(invKey))
                                .then(Mono.fromRunnable(() -> eventPublisher.publishInventoryExpired(r)));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .then();
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    static class HoldRecord {
        private UUID holdId;
        private UUID inventoryId;
        private UUID userId;
        private LocalDateTime from;
        private LocalDateTime to;
        private LocalDateTime expiresAt;
    }

    private Mono<HoldResponse> createHoldForGame(HoldRequest req) {
        UUID gameId = req.getGameId();
        if (gameId == null) {
            return Mono.error(new IllegalArgumentException("gameId is required"));
        }
        return catalogClient.getInventoryForGame(gameId)
                .filter(this::isInventoryAvailable)
                .concatMap(item -> createHoldForInventory(item.getId(), req)
                        .onErrorResume(InventoryUnavailableException.class, ex -> Mono.empty()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalStateException("No inventory available")));
    }

    private Mono<HoldResponse> createHoldForInventory(UUID inventoryId, HoldRequest req) {
        if (inventoryId == null || req.getFrom() == null || req.getTo() == null || req.getUserId() == null) {
            return Mono.error(new IllegalArgumentException("inventoryId, from, to, and userId are required"));
        }

        LocalDateTime from = req.getFrom();
        LocalDateTime to = req.getTo();

        String invKey = inventoryKey(inventoryId, from, to);

        return redis.opsForValue().get(invKey)
                .flatMap(existing -> getHoldRecord(existing)
                        .flatMap(record -> {
                            if (record.getUserId().equals(req.getUserId())) {
                                return Mono.just(new HoldResponse(record.getHoldId(), record.getExpiresAt(), "HELD"));
                            }
                            return Mono.error(new InventoryUnavailableException("Inventory is already held"));
                        }))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            UUID holdId = UUID.randomUUID();
                            String holdKey = holdKey(holdId);

                            return redis.opsForValue().setIfAbsent(invKey, holdId.toString())
                                    .flatMap(ok -> {
                                        if (!ok) {
                                            return Mono.error(new InventoryUnavailableException("Inventory is already held"));
                                        }
                                        return redis.expire(invKey, Duration.ofSeconds(ttlSeconds))
                                                .then(Mono.defer(() -> {
                                                    HoldRecord record = new HoldRecord(holdId, inventoryId,
                                                            req.getUserId(), from, to, LocalDateTime.now().plusSeconds(ttlSeconds));
                                                    String json;
                                                    try {
                                                        json = mapper.writeValueAsString(record);
                                                    } catch (JsonProcessingException e) {
                                                        return Mono.error(e);
                                                    }
                                                    return redis.opsForValue().set(holdKey, json)
                                                            .then(redis.expire(holdKey, Duration.ofSeconds(ttlSeconds)))
                                                            .then(Mono.fromCallable(() -> {
                                                                eventPublisher.publishInventoryHeld(record);
                                                                return new HoldResponse(holdId, record.getExpiresAt(), "HELD");
                                                            }));
                                                }));
                                    });
                        })
                );
    }

    private Mono<HoldRecord> getHoldRecord(String holdIdStr) {
        String key = "hold:" + holdIdStr;
        return redis.opsForValue().get(key)
                .flatMap(json -> {
                    try {
                        HoldRecord r = mapper.readValue(json, HoldRecord.class);
                        return Mono.just(r);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    private boolean isInventoryAvailable(InventoryItemDto item) {
        if (item == null) return false;
        String status = item.getStatus();
        return status == null || status.isBlank() || "AVAILABLE".equalsIgnoreCase(status);
    }

    static class InventoryUnavailableException extends RuntimeException {
        InventoryUnavailableException(String message) {
            super(message);
        }
    }
}
