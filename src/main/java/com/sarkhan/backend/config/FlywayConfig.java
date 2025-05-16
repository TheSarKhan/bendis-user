package com.sarkhan.backend.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flywayForSecondDataSource(
            @Qualifier("secondDataSource") DataSource dataSource) {

        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/second")
                .baselineOnMigrate(true)
                .load();
    }
}
