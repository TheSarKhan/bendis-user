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
public class UserDbInitializer {

    private final DataSource userDataSource;

    public UserDbInitializer(@Qualifier("firstDataSource") DataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection conn = userDataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/user/init.sql"));
            log.info("User DB schema.sql applied successfully.");
        } catch (Exception e) {
            log.error("Failed to apply schema.sql to Product DB: " + e.getMessage());
        }
    }
}
