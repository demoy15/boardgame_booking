package com.demoy.boardbox_catalog.configuration;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public DataSource liquibaseDataSource(@Value("${spring.datasource.url}") String url,
                                          @Value("${spring.datasource.username}") String user,
                                          @Value("${spring.datasource.password}") String pass) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource liquibaseDataSource,
                                     @Value("${spring.liquibase.change-log}") String changelog) {
        SpringLiquibase liq = new SpringLiquibase();
        liq.setDataSource(liquibaseDataSource);
        liq.setChangeLog(changelog);
        liq.setShouldRun(true);
        return liq;
    }
}

