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
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import javax.sql.DataSource; // ya da jakarta.sql.DataSource, amma bütün layihədə uyğun olmalı

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.sarkhan.backend.repository.comment",
        entityManagerFactoryRef = "fourEntityManagerFactory",
        transactionManagerRef = "fourTransactionManager"
)
public class CommentConfig {

    @Value("${spring.datasource.four.url}")
    private String fourDbUrl;

    @Value("${spring.datasource.four.username}")
    private String fourDbUsername;

    @Value("${spring.datasource.four.password}")
    private String fourDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String fourDbDdlAuto;

    @Bean(name = "fourDataSource")
    public DataSource fourDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(fourDbUrl)
                .username(fourDbUsername)
                .password(fourDbPassword)
                .build();

        dataSource.setPoolName("CommentDbHikariPool");
        return dataSource;
    }

    @Bean(name = "fourEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fourEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("fourDataSource") DataSource fourDataSource) {
        return builder
                .dataSource(fourDataSource)
                .packages("com.sarkhan.backend.model.comment")
                .persistenceUnit("four")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "fourTransactionManager")
    public PlatformTransactionManager fourTransactionManager(
            @Qualifier("fourEntityManagerFactory") EntityManagerFactory fourEntityManagerFactory) {
        return new JpaTransactionManager(fourEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", fourDbDdlAuto);
        return properties;
    }
}
