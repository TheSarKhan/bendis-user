package com.sarkhan.backend.config;


import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.sarkhan.backend.repository.order",  // Order repositoriyaların paketi
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderDbConfig {

    @Value("${spring.datasource.sixth.url}")
    private String orderDbUrl;

    @Value("${spring.datasource.sixth.username}")
    private String orderDbUsername;

    @Value("${spring.datasource.sixth.password}")
    private String orderDbPassword;


    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String orderDbDdlAuto;

    @Bean(name = "orderDataSource")
    public DataSource orderDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(orderDbUrl)
                .username(orderDbUsername)
                .password(orderDbPassword)
                .build();

        dataSource.setPoolName("OrderDbHikariPool");
        return dataSource;
    }

    @Bean(name = "orderEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("orderDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.sarkhan.backend.model.order")
                .persistenceUnit("order")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "orderTransactionManager")
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", orderDbDdlAuto);
        return properties;
    }
}

