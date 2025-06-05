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
        basePackages = "com.sarkhan.backend.repository.order",
        entityManagerFactoryRef = "sixthEntityManagerFactory", // Düzəliş
        transactionManagerRef = "sixthTransactionManager"      // Düzəliş
)
public class OrderDbConfig {

    @Value("${spring.datasource.sixth.url}")
    private String sixthDbUrl;

    @Value("${spring.datasource.sixth.username}")
    private String sixthDbUsername;

    @Value("${spring.datasource.sixth.password}")
    private String sixthDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String sixthDbDdlAuto;

    @Bean(name = "sixthDataSource")
    public DataSource thirdDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(sixthDbUrl)
                .username(sixthDbUsername)
                .password(sixthDbPassword)
                .build();

        dataSource.setPoolName("OrderDbHikariPool"); // ✅ Buraya anlamlı pool adı veriyorsun
        return dataSource;
    }

    @Bean(name = "sixthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sixthEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sixthDataSource") DataSource sixthDataSource) {
        return builder
                .dataSource(sixthDataSource)
                .packages("com.sarkhan.backend.model.order")
                // Düzəliş: kiçik hərflə
                .persistenceUnit("sixth")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "sixthTransactionManager")
    public PlatformTransactionManager sixthTransactionManager(
            @Qualifier("sixthEntityManagerFactory") EntityManagerFactory sixthEntityManagerFactory) {
        return new JpaTransactionManager(sixthEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", sixthDbDdlAuto);
        return properties;
    }
}