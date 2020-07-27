package com.rbkmoney.columbus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.contants.CountryCode;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.util.IpAddressUtils;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.geo_ip.GeoIDInfo;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.geo_ip.SubdivisionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.rbkmoney.damsel.geo_ip.geo_ipConstants.GEO_ID_UNKNOWN;

@Slf4j
@RequiredArgsConstructor
public class GeoIpServiceHandler implements GeoIpServiceSrv.Iface {

    private ObjectMapper mapper = new ObjectMapper();
    private static String UNKNOWN = "UNKNOWN";

    private final GeoService service;

    @Override
    public LocationInfo getLocation(String ip) throws InvalidRequest, TException {
        if (!IpAddressUtils.isValid(ip)) {
            throw new InvalidRequest(Arrays.asList(ip));
        }

        CityResponse cityResponse = null;
        String json = "";
        try {
            cityResponse = service.getLocationByIp(IpAddressUtils.convert(ip));
            json = mapper.writeValueAsString(cityResponse);
        } catch (AddressNotFoundException e) {
            log.info("IP address {} not found in maxmind db.", ip);
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
        List<String> invalidIps = set.stream().filter(ip -> !IpAddressUtils.isValid(ip)).collect(Collectors.toList());
        if (!invalidIps.isEmpty()) {
            throw new InvalidRequest(invalidIps);
        }

        Map<String, LocationInfo> map = new HashMap<>();
        for (String ip : set) {
            map.put(ip, getLocation(ip));
        }

        return map;
    }

    @Override
    public Map<Integer, GeoIDInfo> getLocationInfo(Set<Integer> geoIds, String lang) throws InvalidRequest, TException {
        List<CityLocation> cityLocations = service.getLocationName(geoIds, lang);
        Map<Integer, GeoIDInfo> result = new HashMap<>();
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
    public Map<Integer, String> getLocationName(Set<Integer> geoIds, String lang) throws InvalidRequest, TException {
        return service.getLocationName(geoIds, lang).stream()
                .collect(Collectors.toMap(CityLocation::getGeonameId, CityLocation::getName));
    }

    @Override
    public String getLocationIsoCode(String ip) throws InvalidRequest, TException {
        if (!IpAddressUtils.isValid(ip)) {
            throw new InvalidRequest(Collections.singletonList(ip));
        }
        try {
            CityResponse cityResponse = service.getLocationByIp(IpAddressUtils.convert(ip));
            if (cityResponse != null && cityResponse.getCountry().getIsoCode() != null) {
                CountryCode alpha2Code = CountryCode.getByAlpha2Code(cityResponse.getCountry().getIsoCode());
                if (alpha2Code == null) {
                    log.warn("Unknown iso code, isoCode={}, ip={}", cityResponse.getCountry().getIsoCode(), ip);
                    return UNKNOWN;
                }
                return alpha2Code.getAlpha3();
            }
        } catch (AddressNotFoundException e) {
            log.info("IP address {} not found in maxmind db.", ip);
        } catch (Exception e) {
            logAndThrow("Unknown exception.", e);
        }
        return UNKNOWN;
    }

    private void logAndThrow(String message, Exception e) throws TException {
        log.error(message, e);
        throw new TException(message, e);
    }
}
