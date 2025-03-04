package com.sarkhan.backend.config;



import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.sarkhan.backend.repository.user",
        entityManagerFactoryRef = "firstEntityManagerFactory",
        transactionManagerRef = "firstTransactionManager"
)
public class UserDbConfig {

    @Value("${spring.datasource.first.url}")
    private String firstDbUrl;

    @Value("${spring.datasource.first.username}")
    private String firstDbUsername;

    @Value("${spring.datasource.first.password}")
    private String firstDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String firstDbDdlAuto;


    @Primary
    @Bean(name = "firstDataSource")
    public DataSource firstDataSource() {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.url(firstDbUrl);
        builder.username(firstDbUsername);
        builder.password(firstDbPassword);
        return builder.build();
    }


    @Primary
    @Bean(name = "firstEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(firstDataSource())
                .packages("com.sarkhan.backend.model.user")
                .persistenceUnit("first")
                .properties(hibernateProperties())
                .build();
    }

    @Primary
    @Bean(name = "firstTransactionManager")
    public PlatformTransactionManager firstTransactionManager(
            @Qualifier("firstEntityManagerFactory") EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", firstDbDdlAuto);
        return properties;
    }
}
