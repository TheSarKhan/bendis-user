package com.sarkhan.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.sarkhan.backend.repository.seller",
        entityManagerFactoryRef = "fourthEntityManagerFactory",
        transactionManagerRef = "fourthTransactionManager"
)
public class SellerDbConfig {

    @Value("${spring.datasource.fourth.url}")
    private String fourthDbUrl;

    @Value("${spring.datasource.fourth.username}")
    private String fourthDbUsername;

    @Value("${spring.datasource.fourth.password}")
    private String fourthDbPassword;

    @Bean(name = "fourthDataSource", destroyMethod = "close")
    public HikariDataSource fourthDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(fourthDbUrl);
        dataSource.setUsername(fourthDbUsername);
        dataSource.setPassword(fourthDbPassword);
        dataSource.setPoolName("SellerDbHikariPool");
        return dataSource;
    }

    @Bean(name = "fourthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fourthEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("fourthDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.sarkhan.backend.model.seller")
                .persistenceUnit("fourth")
                .properties(Map.of(
                        "hibernate.hbm2ddl.auto", "update",
                        "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"
                ))
                .build();
    }


    @Bean(name = "fourthTransactionManager")
    public PlatformTransactionManager fourthTransactionManager(
            @Qualifier("fourthEntityManagerFactory") EntityManagerFactory fourthEntityManagerFactory) {
        return new JpaTransactionManager(fourthEntityManagerFactory);
    }
}
