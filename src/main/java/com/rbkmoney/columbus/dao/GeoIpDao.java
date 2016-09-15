package com.rbkmoney.columbus.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.model.GeoNameIdInfo;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.model.LocationInfo;
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
import java.util.HashMap;
import java.util.Map;


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

    public LocationInfo getLocationInfoByIp(String ip) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            log.error("Cant parse ip address", e);
            return buildUndefinedLocation();
        }

        try {
            CityResponse response = reader.city(ipAddress);
            LocationInfo locationInfo = buildLocationInfo(response);
            String jsonRawResponse = mapper.writeValueAsString(response);
            locationInfo.setRawResponse(jsonRawResponse);
            return locationInfo;
        } catch (IOException e) {
            log.error("DB file access error", e);
        } catch (GeoIp2Exception e) {
            log.error("GEO DB error", e);
        }
        return buildUndefinedLocation();
    }


    private LocationInfo buildLocationInfo(CityResponse cityResponse) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCity(new GeoNameIdInfo(
                cityResponse.getCity().getGeoNameId(),
                mapNames(cityResponse.getCity().getNames()),
                GeoNameIdInfo.GeoNameType.CITY
        ));
        locationInfo.setSubdivision(new GeoNameIdInfo(
                cityResponse.getLeastSpecificSubdivision().getGeoNameId(),
                mapNames(cityResponse.getLeastSpecificSubdivision().getNames()),
                GeoNameIdInfo.GeoNameType.SUBDIVISION
        ));
        locationInfo.setCountry(new GeoNameIdInfo(
                cityResponse.getCountry().getGeoNameId(),
                mapNames(cityResponse.getCountry().getNames()),
                GeoNameIdInfo.GeoNameType.COUNTRY
        ));
        locationInfo.setLatitude(cityResponse.getLocation().getLatitude());
        locationInfo.setLongitude(cityResponse.getLocation().getLongitude());
        locationInfo.setConfidence(cityResponse.getCity().getConfidence());
        locationInfo.setTimeZone(cityResponse.getLocation().getTimeZone());
        return locationInfo;
    }

    private Map<Lang, String> mapNames(Map<String, String> names) {
        HashMap<Lang, String> result = new HashMap<>();
        names.entrySet().stream().forEach(entry -> {
            result.put(Lang.getByAbbreviation(entry.getKey()), entry.getValue());
        });
        return result;
    }

    private LocationInfo buildUndefinedLocation() {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCountry(GeoNameIdInfo.buildUndefined());
        locationInfo.setSubdivision(GeoNameIdInfo.buildUndefined());
        locationInfo.setCity(GeoNameIdInfo.buildUndefined());
        locationInfo.setTimeZone("UNDEFINED");
        return locationInfo;
    }
}
