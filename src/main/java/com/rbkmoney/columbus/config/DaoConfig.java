package com.rbkmoney.columbus.config;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.CityLocationsDaoImpl;
import org.jooq.Schema;
import org.jooq.impl.SchemaImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class DaoConfig {

    @Bean
    public CityLocationsDao geoDao(DataSource ds) {
        return new CityLocationsDaoImpl(ds);
    }

    @Bean
    public Schema dbSchema() {
        return new SchemaImpl("clb");
    }
}
