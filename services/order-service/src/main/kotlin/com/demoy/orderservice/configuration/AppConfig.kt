package com.demoy.orderservice.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
@EnableScheduling
class AppConfig {
    @Bean
    fun r2dbcTransactionManager(cf: ConnectionFactory): ReactiveTransactionManager =
        R2dbcTransactionManager(cf)

    @Bean
    fun transactionalOperator(txManager: ReactiveTransactionManager): TransactionalOperator =
        TransactionalOperator.create(txManager)
}