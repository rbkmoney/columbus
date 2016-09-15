package com.rbkmoney.columbus.config;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.CityLocationsDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class DaoConfig {

    @Bean
    @DependsOn("dbInitializer")
    public CityLocationsDao geoDao(DataSource ds) {
        return new CityLocationsDaoImpl(ds);
    }
}
