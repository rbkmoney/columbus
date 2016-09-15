package com.rbkmoney.columbus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Schema;
import org.jooq.impl.SchemaImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Configuration
public class DataSourceConfiguration {

    @Value("${db.jdbc.driver}")
    private String jdbcDriver;

    @Value("${db.jdbc.url}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.pool.size}")
    private Integer poolSize;

    @Value("${db.pool.idleTimeoutMs}")
    private Long idleTimeoutMs;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(jdbcDriver);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        HikariDataSource dataSource = new HikariDataSource(config);
        dataSource.setMaximumPoolSize(poolSize);
        dataSource.setIdleTimeout(idleTimeoutMs);
        return dataSource;
    }

    @Bean
    public Schema dbSchema() {
        return new SchemaImpl("columb");
    }

}
