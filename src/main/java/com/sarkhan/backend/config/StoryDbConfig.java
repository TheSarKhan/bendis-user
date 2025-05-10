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
        basePackages = "com.sarkhan.backend.repository.story",
        entityManagerFactoryRef = "thirdEntityManagerFactory",
        transactionManagerRef = "thirdTransactionManager"
)
public class StoryDbConfig {

    @Value("${spring.datasource.third.url}")
    private String thirdDbUrl;

    @Value("${spring.datasource.third.username}")
    private String thirdDbUsername;

    @Value("${spring.datasource.third.password}")
    private String thirdDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String thirdDbDdlAuto;


    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(thirdDbUrl)
                .username(thirdDbUsername)
                .password(thirdDbPassword)
                .build();

        dataSource.setPoolName("StoryDbHikariPool"); // ✅ Buraya anlamlı pool adı veriyorsun
        return dataSource;
    }


    @Bean(name = "thirdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thirdEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("thirdDataSource") DataSource thirdDataSource) {
        return builder
                .dataSource(thirdDataSource)
                .packages("com.sarkhan.backend.model.story")
                .persistenceUnit("third")
                .properties(hibernateProperties())
                .build();
    }


    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager(
            @Qualifier("thirdEntityManagerFactory") EntityManagerFactory thirdEntityManagerFactory) {
        return new JpaTransactionManager(thirdEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", thirdDbDdlAuto);
        return properties;
    }
}


