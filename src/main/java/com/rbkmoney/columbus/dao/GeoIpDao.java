package com.rbkmoney.columbus.dao;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;


/**
 * Read information from .mmdb file MaxMind db
 */
@Component
public class GeoIpDao {

    @Value(value = "${geo.db.file.path}")
    private String geoDbFilePath;

    @Autowired
    private ResourceLoader resourceLoader;

    private DatabaseReader reader;

    @PostConstruct
    public void init() throws IOException {
        // A File object pointing to your GeoIP2 or GeoLite2 database
        Resource resource = resourceLoader.getResource(geoDbFilePath);
        InputStream inputStream = resource.getInputStream();
        reader = new DatabaseReader.Builder(inputStream).build();
    }

    public CityResponse getLocationInfoByIp(InetAddress ipAddress) throws IOException, GeoIp2Exception {
        return reader.city(ipAddress);
    }
}
