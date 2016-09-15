package com.rbkmoney.columbus.service;

import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.model.LocationInfo;
import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoService {

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoIpDao geoIpDao;

    public void enrich(){
        String ip = "94.159.54.234";
        LocationInfo locationInfoByIp = geoIpDao.getLocationInfoByIp(ip);
    }

    public CityLocation getLocationByGeoId(int geoId ){
        return cityLocationsDao.getByGeoId(geoId, Lang.RU);
    }
}
