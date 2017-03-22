package com.rbkmoney.columbus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.util.IpAddresUtils;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.geo_ip.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.damsel.geo_ip.geo_ipConstants.GEO_ID_UNKNOWN;

public class GeoIpServiceHandler implements GeoIpServiceSrv.Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectMapper mapper = new ObjectMapper();
    private GeoService service;
    private static String UNKNOWN = "UNKNOWN";

    public GeoIpServiceHandler(GeoService service) {
        this.service = service;
    }

    @Override
    public LocationInfo getLocation(String ip) throws InvalidRequest, TException {
        if (!IpAddresUtils.isValid(ip)) {
            throw new InvalidRequest(Arrays.asList(ip));
        }

        CityResponse cityResponse = null;
        String json = "";
        try {
            cityResponse = service.getLocationByIp(IpAddresUtils.convert(ip));
            json = mapper.writeValueAsString(cityResponse);
        } catch (AddressNotFoundException e) {
            log.warn("IP address {} not found in maxmind db.");
        } catch (JsonProcessingException e) {
            logAndThrow("CityResponse cannot be converted to JSON.", e);
        } catch (IOException | GeoIp2Exception e) {
            logAndThrow("Unknown IO exception.", e);
        }

        int cityId = GEO_ID_UNKNOWN;
        int countryId = GEO_ID_UNKNOWN;
        if (cityResponse != null && cityResponse.getCity().getGeoNameId() != null) {
            cityId = cityResponse.getCity().getGeoNameId();
        }
        if (cityResponse != null && cityResponse.getCountry().getGeoNameId() != null) {
            countryId = cityResponse.getCountry().getGeoNameId();
        }
        LocationInfo locationInfo = new LocationInfo(cityId, countryId);
        locationInfo.setRawResponse(json);
        return locationInfo;
    }

    @Override
    public Map<String, LocationInfo> getLocations(Set<String> set) throws InvalidRequest, TException {
        List<String> invalidIps = set.stream().filter(ip -> !IpAddresUtils.isValid(ip)).collect(Collectors.toList());
        if (invalidIps.size() > 0) {
            throw new InvalidRequest(invalidIps);
        }

        Map<String, LocationInfo> map = new HashMap<>();
        for (String ip : set) {
            map.put(ip, getLocation(ip));
        }

        return map;
    }

    @Override
    public Map<Integer, GeoIDInfo> getLocationInfo(Set<Integer> geo_ids, String lang) throws InvalidRequest, TException {
        List<CityLocation> cityLocations = service.getLocationName(geo_ids, lang);
        Map<Integer, GeoIDInfo> result = new HashMap<Integer, GeoIDInfo>();
        cityLocations.forEach(cl -> {
            GeoIDInfo geoIDInfo = new GeoIDInfo(cl.getCountryName());
            geoIDInfo.setCityName(cl.getCityName());

            Set<SubdivisionInfo> subdivisionInfoSet = new HashSet<>();
            if (!StringUtils.isEmpty(cl.getSubdivision1Name())) {
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1, cl.getSubdivision1Name()));
            }
            if (!StringUtils.isEmpty(cl.getSubdivision1Name())) {
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1, cl.getSubdivision1Name()));
            }
            if (!subdivisionInfoSet.isEmpty()) {
                geoIDInfo.setSubdivisions(subdivisionInfoSet);
            }
            result.put(cl.getGeonameId(), geoIDInfo);
        });
        return result;
    }

    //* Если передан неизвестный geoID, он не попадет в возвращаемый результат
    @Override
    public Map<Integer, String> getLocationName(Set<Integer> geo_ids, String lang) throws InvalidRequest, TException {
        return service.getLocationName(geo_ids, lang).stream()
                .collect(Collectors.toMap(CityLocation::getGeonameId, CityLocation::getName));

    }

    public static GeoIDInfo buildUnknownGeoIdInfo() {
        GeoIDInfo geoIDInfo = new GeoIDInfo();
        geoIDInfo.setCountryName(UNKNOWN);
        return geoIDInfo;
    }

    /*
    * put(key, emptyValue) for keys from set which are not exist in map.keySet()
    * */
    private <K, V> Map<K, V> putEmptyValues(Map<K, V> map, Set<K> set, V emptyValue) {
        Set<K> nullKeys = new HashSet<>(set);
        nullKeys.removeAll(map.keySet());

        Map<K, V> resultMap = new HashMap<>(map);

        for (K key : nullKeys) {
            resultMap.put(key, emptyValue);
        }

        return resultMap;
    }

    private void logAndThrow(String message, Exception e) throws TException {
        log.error(message, e);
        throw new TException(message, e);
    }
}
