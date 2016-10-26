package com.rbkmoney.columbus.service;

import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.CityResponseWrapper;
import com.rbkmoney.damsel.geo_ip.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GeoIpServiceHandler implements GeoIpServiceSrv.Iface {
    //TODO move to thrift
    public static final int UNDEFINED_GEO_ID = -1;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private GeoService service;

    public GeoIpServiceHandler(GeoService service) {
        this.service = service;
    }

    @Override
    public LocationInfo getLocation(String ip) throws CantDetermineLocation, TException {
        CityResponseWrapper cityResponse = service.getLocationByIp(ip);
        if(cityResponse == null){
            throw new CantDetermineLocation();
        }

        int cityId = UNDEFINED_GEO_ID;
        int countryId = UNDEFINED_GEO_ID;
        if(cityResponse.getResponse().getCity().getGeoNameId() != null){
            cityId = cityResponse.getResponse().getCity().getGeoNameId();
        }
        if(cityResponse.getResponse().getCountry().getGeoNameId() != null){
            countryId = cityResponse.getResponse().getCountry().getGeoNameId();
        }

        return new LocationInfo(cityId, countryId, cityResponse.getJsonResponse());
    }

    @Override
    public Map<Integer, GeoIDInfo> getLocationInfo(Set<Integer> geo_ids, String lang) throws TException {
        List<CityLocation> cityLocations = service.getLocationName(geo_ids, lang);

        Map<Integer, GeoIDInfo> map = cityLocations.stream().map(cl -> {
            GeoIDInfo geoIDInfo = new GeoIDInfo(cl.getGeonameId(), cl.getCountryName());
            geoIDInfo.setCityName(cl.getCityName());

            Set<SubdivisionInfo> subdivisionInfoSet = new HashSet<>();
            if(!StringUtils.isEmpty(cl.getSubdivision1Name())){
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1,cl.getSubdivision1Name()));
            }
            if(!StringUtils.isEmpty(cl.getSubdivision1Name())){
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1,cl.getSubdivision1Name()));
            }
            if(!subdivisionInfoSet.isEmpty()){
                geoIDInfo.setSubdivisions(subdivisionInfoSet);
            }

            return geoIDInfo;
        }).collect(Collectors.toMap(GeoIDInfo::getGeonameId, v->v));

        return putEmptyValues(map, geo_ids, null);
    }

    @Override
    public Map<Integer, String> getLocationName(Set<Integer> geo_ids, String lang) throws TException {
        Map<Integer, String> map = service.getLocationName(geo_ids, lang).stream()
                .collect(Collectors.toMap(CityLocation::getGeonameId, CityLocation::getName));
        return putEmptyValues(map, geo_ids, null);
    }


    /*
    * put(key, emptyValue) for keys from set which are not exist in map.keySet()
    * */
    private <K,V> Map<K, V> putEmptyValues(Map<K, V> map, Set<K> set, V emptyValue){
        Set<K> nullKeys = new HashSet<>(set);
        nullKeys.removeAll(map.keySet());

        Map<K,V> resultMap = new HashMap<>(map);

        for(K key: nullKeys){
            resultMap.put(key, emptyValue);
        }

        return resultMap;
    }
}
