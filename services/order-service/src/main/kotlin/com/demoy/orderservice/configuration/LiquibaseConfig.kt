package com.demoy.orderservice.configuration

import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(LiquibaseProperties::class)
class LiquibaseConfig(
    private val liquibaseProperties: LiquibaseProperties,

    @Value("\${spring.datasource.url}")
    private val jdbcUrl: String,

    @Value("\${spring.datasource.username}")
    private val jdbcUser: String,

    @Value("\${spring.datasource.password}")
    private val jdbcPassword: String,

    @Value("\${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private val jdbcDriver: String,

    @Value("\${spring.liquibase.default-schema:}")
    private val liquibaseDefaultSchema: String
) {

    @Bean
    fun liquibase(): SpringLiquibase {
        val ds: DataSource = DriverManagerDataSource().apply {
            setDriverClassName(jdbcDriver)
            url = jdbcUrl
            username = jdbcUser
            password = jdbcPassword
        }

        return SpringLiquibase().apply {
            dataSource = ds
            changeLog = liquibaseProperties.changeLog
            liquibaseProperties.contexts?.let { setContexts(it.joinToString(",")) }
            liquibaseProperties.defaultSchema?.let { defaultSchema = it }
            isDropFirst = liquibaseProperties.isDropFirst
            setShouldRun(liquibaseProperties.isEnabled)
        }
    }
}
