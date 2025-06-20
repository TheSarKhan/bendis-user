package com.sarkhan.backend.init;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component
public class ProductDbInitializer {

    private final DataSource productDataSource;

    public ProductDbInitializer(@Qualifier("secondDataSource") DataSource productDataSource) {
        this.productDataSource = productDataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection conn = productDataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/product/init.sql"));
            log.info("Product DB schema.Sql applied successfully.");
        } catch (Exception e) {
            log.error("Failed to apply schema.Sql to Product DB: " + e.getMessage());
        }
    }
}
