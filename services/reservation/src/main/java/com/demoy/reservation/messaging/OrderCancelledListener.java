package com.demoy.reservation.messaging;

import com.demoy.reservation.configuration.RabbitConfig;
import com.demoy.reservation.service.HoldService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelledListener {

    private final HoldService holdService;
    private final ObjectMapper mapper;

    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    public void onOrderCancelled(String body) {
        try {
            JsonNode node = mapper.readTree(body);
            String holdId = node.get("holdId").asText();
            holdService.cancelHold(holdId).subscribe();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

