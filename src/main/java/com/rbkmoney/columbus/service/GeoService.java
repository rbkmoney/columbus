package com.rbkmoney.columbus.service;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.CityResponseWrapper;
import com.rbkmoney.columbus.model.Lang;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GeoService {

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoIpDao geoIpDao;

    public CityResponseWrapper getLocationByIp(String ip){
        return geoIpDao.getLocationInfoByIp(ip);
    }

    public List<CityLocation> getLocationName(Set<Integer> geoIdset, String lang) throws TException {
        return cityLocationsDao.getByGeoIds(geoIdset, Lang.getByAbbreviation(lang));
    }
}
