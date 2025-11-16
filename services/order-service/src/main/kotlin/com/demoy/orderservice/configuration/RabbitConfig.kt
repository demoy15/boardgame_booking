package com.demoy.orderservice.configuration

import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        const val DOMAIN_EXCHANGE = "domain.events"
        const val ORDER_CANCELLED_ROUTING = "order.cancelled"
    }

    @Bean
    fun domainExchange() = TopicExchange(DOMAIN_EXCHANGE, true, false)
}