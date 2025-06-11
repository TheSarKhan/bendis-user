package com.sarkhan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sarkhan.backend.mapper.seller", "com.sarkhan.backend"})
@EnableJpaRepositories(basePackages = "com.sarkhan.backend.repository.seller")
@EntityScan(basePackages = "com.sarkhan.backend.model")
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}

