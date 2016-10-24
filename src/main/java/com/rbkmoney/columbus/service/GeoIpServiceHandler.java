package com.rbkmoney.columbus.service;

import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.damsel.geo_ip.*;
import org.apache.thrift.TException;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoIpServiceHandler implements GeoIpServiceSrv.Iface {

    private GeoService service;

    public GeoIpServiceHandler(GeoService service) {
        this.service = service;
    }

    @Override
    public LocationInfo getLocation(String ip) throws CantDetermineLocation, TException {
        //TODO think about CantDetermineLocation instead of UndefinedLocation
        com.rbkmoney.columbus.model.LocationInfo info = service.getLocationByIp(ip);
        LocationInfo ret = new LocationInfo(
                info.getCity().getGeoNameId(),
                info.getCountry().getGeoNameId(),
                info.getRawResponse()
        );
        return ret;
    }

    @Override
    public Map<Integer, GeoIDInfo> getLocationInfo(Set<Integer> geo_ids, String lang) throws TException {
        List<CityLocation> cityLocations = service.getLocationName(geo_ids, lang);

        return cityLocations.stream().map(cl -> {
            GeoIDInfo geoIDInfo = new GeoIDInfo(cl.getGeonameId(), cl.getCountryName());
            geoIDInfo.setCityName(cl.getCityName());

            Set<SubdivisionInfo> subdivisionInfoSet = new HashSet<>();
            if(!StringUtils.isEmpty(cl.getSubdivision_1Name())){
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1,cl.getSubdivision_1Name()));
            }
            if(!StringUtils.isEmpty(cl.getSubdivision_1Name())){
                subdivisionInfoSet.add(new SubdivisionInfo((short) 1,cl.getSubdivision_1Name()));
            }
            if(!subdivisionInfoSet.isEmpty()){
                geoIDInfo.setSubdivisions(subdivisionInfoSet);
            }

            return geoIDInfo;
        }).collect(Collectors.toMap(GeoIDInfo::getGeonameId, v->v));
    }

    @Override
    public Map<Integer, String> getLocationName(Set<Integer> geo_ids, String lang) throws TException {
        return service.getLocationName(geo_ids, lang).stream()
                .collect(Collectors.toMap(CityLocation::getGeonameId, CityLocation::getName));
    }


}
