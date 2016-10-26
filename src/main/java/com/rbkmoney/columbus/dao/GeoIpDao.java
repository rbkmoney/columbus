package com.rbkmoney.columbus.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.model.CityResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Read information from .mmdb file MaxMind db
 */
@Component
public class GeoIpDao {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${geo.db.file.path}")
    private String geoDbFilePath;

    @Autowired
    private ResourceLoader resourceLoader;

    private DatabaseReader reader;
    private ObjectMapper mapper;

    @PostConstruct
    public void init() throws IOException {
        // A File object pointing to your GeoIP2 or GeoLite2 database
        Resource resource = resourceLoader.getResource(geoDbFilePath);
        File dbAsFile = resource.getFile();
        reader = new DatabaseReader.Builder(dbAsFile).build();
        mapper = new ObjectMapper();
    }

    public CityResponseWrapper getLocationInfoByIp(String ip) {
        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            log.error("Cant parse ip address", e);
            return emptyCityResponse();
        }

        try {
            CityResponse response = reader.city(ipAddress);
            String jsonRawResponse = mapper.writeValueAsString(response);
            return new CityResponseWrapper(response, jsonRawResponse);
        } catch (IOException e) {
            log.error("DB file access error", e);
        } catch (GeoIp2Exception e) {
            log.error("GEO DB error", e);
        }
        return emptyCityResponse();
    }

    private static CityResponseWrapper emptyCityResponse(){
        return new CityResponseWrapper(null, "");
    }
}
