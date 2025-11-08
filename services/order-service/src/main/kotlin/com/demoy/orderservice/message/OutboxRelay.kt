package com.demoy.orderservice.message

import com.demoy.orderservice.repository.OutboxRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Component
class OutboxRelay(
    private val outboxRepo: OutboxRepository,
    private val rabbit: RabbitTemplate
) {
    private val exchange = "domain.events"

    @Scheduled(fixedDelay = 2000)
    fun relay() {
        outboxRepo.findAllByPublishedFalse()
            .publishOn(Schedulers.boundedElastic())
            .flatMap { outbox ->
                try {
                    rabbit.convertAndSend(exchange, outbox.eventType, outbox.payload)
                    val updated = outbox.copy(published = true, publishedAt = LocalDateTime.now())
                    outboxRepo.save(updated)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    reactor.core.publisher.Mono.empty()
                }
            }
            .subscribe()
    }
}