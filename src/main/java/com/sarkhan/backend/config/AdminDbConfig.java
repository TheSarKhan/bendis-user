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
        basePackages = "com.sarkhan.backend.repository.admin",
        entityManagerFactoryRef = "seventhEntityManagerFactory",
        transactionManagerRef = "seventhTransactionManager"
)
public class AdminDbConfig {
    @Value("${spring.datasource.seventh.url}")
    private String seventhDbUrl;

    @Value("${spring.datasource.seventh.username}")
    private String seventhDbUsername;

    @Value("${spring.datasource.seventh.password}")
    private String seventhDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String seventhDbDdlAuto;


    @Bean(name = "seventhDataSource")
    public DataSource seventhDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(seventhDbUrl)
                .username(seventhDbUsername)
                .password(seventhDbPassword)
                .build();

        dataSource.setPoolName("AdminDbHikariPool");
        return dataSource;
    }


    @Bean(name = "seventhEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean seventhEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("seventhDataSource") DataSource seventhDataSource) {
        return builder
                .dataSource(seventhDataSource)
                .packages("com.sarkhan.backend.model.admin")
                .persistenceUnit("seventh")
                .properties(hibernateProperties())
                .build();
    }


    @Bean(name = "seventhTransactionManager")
    public PlatformTransactionManager seventhTransactionManager(
            @Qualifier("seventhEntityManagerFactory") EntityManagerFactory seventhEntityManagerFactory) {
        return new JpaTransactionManager(seventhEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", seventhDbDdlAuto);
        return properties;
    }

}
