package com.rbkmoney.columbus.config;

import com.rbkmoney.columbus.service.GeoIpServiceHandler;
import com.rbkmoney.columbus.service.GeoService;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ServiceConfiguration {

    @Bean
    public GeoIpServiceSrv.Iface eventRepoHandler(GeoService service) {
        return new GeoIpServiceHandler(service);
    }
}
