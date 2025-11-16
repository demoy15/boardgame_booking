package com.demoy.reservation.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String DOMAIN_EXCHANGE = "domain.events";
    public static final String ORDER_CANCELLED_ROUTING = "order.cancelled";
    public static final String ORDER_CANCEL_QUEUE = "reservation.order.cancel.queue";

    @Bean
    public TopicExchange domainExchange() {
        return new TopicExchange(DOMAIN_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCancelQueue() {
        return new Queue(ORDER_CANCEL_QUEUE, true);
    }

    @Bean
    public Binding bindOrderCancelled(Queue orderCancelQueue, TopicExchange domainExchange) {
        return BindingBuilder.bind(orderCancelQueue).to(domainExchange).with(ORDER_CANCELLED_ROUTING);
    }
}
