package com.demoy.reservation.service;

import com.demoy.reservation.dto.HoldRequest;
import com.demoy.reservation.dto.HoldResponse;
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
        UUID inventoryId = req.getInventoryId();
        LocalDateTime from = req.getFrom();
        LocalDateTime to = req.getTo();

        String invKey = inventoryKey(inventoryId, from, to);

        return redis.opsForValue().get(invKey)
                .flatMap(existing -> {
                    try {
                        UUID existingHoldId = UUID.fromString(existing);
                        return getHoldById(existingHoldId.toString());
                    } catch (Exception ex) {
                        return Mono.error(new IllegalStateException("Invalid existing hold value"));
                    }
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            UUID holdId = UUID.randomUUID();
                            String holdKey = holdKey(holdId);

                            return redis.opsForValue().setIfAbsent(invKey, holdId.toString())
                                    .flatMap(ok -> {
                                        if (!ok) {
                                            return redis.opsForValue().get(invKey)
                                                    .flatMap(existing -> getHoldById(existing));
                                        }
                                        return redis.expire(invKey, Duration.ofSeconds(ttlSeconds))
                                                .then(Mono.defer(() -> {
                                                    HoldRecord record = new HoldRecord(holdId, req.getInventoryId(),
                                                            req.getUserId(), req.getFrom(), req.getTo(), LocalDateTime.now().plusSeconds(ttlSeconds));
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
}
