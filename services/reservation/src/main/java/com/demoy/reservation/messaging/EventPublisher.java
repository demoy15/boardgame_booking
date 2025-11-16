package com.demoy.reservation.messaging;

import com.demoy.reservation.configuration.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbit;
    private final ObjectMapper mapper;

    public void publishInventoryHeld(Object event) {
        try {
            String payload = mapper.writeValueAsString(event);
            rabbit.convertAndSend(RabbitConfig.DOMAIN_EXCHANGE, "inventory.held", payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishInventoryExpired(Object event) {
        try {
            String payload = mapper.writeValueAsString(event);
            rabbit.convertAndSend(RabbitConfig.DOMAIN_EXCHANGE, "inventory.expired", payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
